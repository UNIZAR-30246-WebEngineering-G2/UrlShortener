package urlshortener.common.web;

import com.google.common.hash.Hashing;

import com.maxmind.geoip2.record.Location;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.domain.Click;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UrlShortenerController {
	private static final Logger LOG = LoggerFactory.getLogger(UrlShortenerController.class);

	private IPService ipService = new IPService();

	@Autowired
	protected ShortURLRepository shortURLRepository;
	@Autowired
	protected ClickRepository clickRepository;

	@RequestMapping(value = "/{id:(?!link).*}", method = RequestMethod.GET)
	public Object redirectTo(@PathVariable String id,
										HttpServletRequest request, RedirectAttributes ra) {
		ShortURL l = shortURLRepository.findByKey(id);
		if (l != null && l.getActive()) {
			createAndSaveClick(id, extractIP(request));
			return createSuccessfulRedirectToResponse(l, request, id);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	private void createAndSaveClick(String hash, String ip) {
		ArrayList<String> locations = obtainLocation(ip);

		Click cl = new Click(null, hash, new Timestamp(System.currentTimeMillis()),
				null, null, null, ip, null, locations.get(0), locations.get(1));
		cl=clickRepository.save(cl);
		LOG.info(cl!=null?"["+hash+"] saved with id ["+cl.getId()+"]":"["+hash+"] was not saved");
	}

	protected ArrayList<String> obtainLocation(String ip){
		String latitude = "IP not in DB";
		String longitude = "IP not in DB";

		Location clientLocation = ipService.obtainLocation(ip);

		if(clientLocation != null){
			latitude = String.valueOf(clientLocation.getLatitude());
			longitude = String.valueOf(clientLocation.getLongitude());

			LOG.info("Latitud of new visitor: " + latitude);
			LOG.info("Longitude of new visitor: " + longitude);

		} else LOG.error("Information about IP " + ip + " not found");

		ArrayList<String> locationArray = new ArrayList<>();
		locationArray.add(latitude);
		locationArray.add(longitude);

		return locationArray;
	}

	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
											  @RequestParam(value = "sponsor", required = false) String sponsor,
											  @RequestParam(value = "publicity-url", required = false) String urlPublicity,
											  @RequestParam(value = "time-publicity", required = false) Integer timePublicity,
											  HttpServletRequest request, RedirectAttributes ra, CheckUrls checkUrls) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		if(urlValidator.isValid(url)){

				String id = (String) request.getSession().getAttribute("user");
				if(id == null) id="";
				ShortURL su = createAndSaveIfValid(url, sponsor, urlPublicity, timePublicity ,id, extractIP(request),ra);
				if (su != null) {
                    HttpHeaders h = new HttpHeaders();
					h.setLocation(su.getUri());
					if(!su.getOwner().equals(id)){
						//Shortened URL already exists
						LOG.error("Extended URL requested has already been shortened");
						return new ResponseEntity<>(su, h,HttpStatus.CONFLICT);
					} else{
                        boolean active = isReachable(url);
                        su.setActive(active);
                        checkUrls.agnadirUrl(su);
                        su.setLastChange(new Timestamp(Calendar.getInstance().getTime().getTime()));
                        if(su.getActive()){
                            return new ResponseEntity<>(su, h, HttpStatus.CREATED);
                        } else return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

					}
				}
				else {
					LOG.error("Couldn't save new shortened URL");
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}

		} else{
			LOG.error("The entered URL is not valid");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	protected String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l, HttpServletRequest request, String id) {
		boolean lessThanTimeEstablished = false;
        final int TIME_ESTABLISHED = 30;
        long time;
        HttpHeaders h = new HttpHeaders();
        Cookie click = findCookie(id,request);
		if(click == null){
            h.set("Set-Cookie",id+"="+System.currentTimeMillis());
        } else {
            SimpleDateFormat yearmonthday = new SimpleDateFormat("yyyy:MM:dd");
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
            SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
            SimpleDateFormat secondFormat = new SimpleDateFormat("ss");

            Timestamp beforeLastClick = new Timestamp(Long.parseLong(click.getValue()));
            String beforeLastClickDate = yearmonthday.format(beforeLastClick);
            String beforeLastClickHour = hourFormat.format(beforeLastClick);
            String beforeLastClickMinute = minuteFormat.format(beforeLastClick);
            String beforeLastClickSeconds = secondFormat.format(beforeLastClick);

            time = System.currentTimeMillis();

            Timestamp lastClick = new Timestamp(time);
            String lastClickDate = yearmonthday.format(lastClick);
            String lastClickHour = hourFormat.format(lastClick);
            String lastClickMinute = minuteFormat.format(lastClick);
            String lastClickSeconds = secondFormat.format(lastClick);

            lessThanTimeEstablished = beforeLastClickDate.equals(lastClickDate) &&
                    beforeLastClickHour.equals(lastClickHour) &&
                    beforeLastClickMinute.equals(lastClickMinute) &&
                    (Integer.parseInt(lastClickSeconds)-Integer.parseInt(beforeLastClickSeconds))<TIME_ESTABLISHED;
            h.set("Set-Cookie",id+"="+time);
        }

		if(l.getSponsor() != null && !lessThanTimeEstablished){
			h.setLocation(URI.create("/publicity"));
			request.getSession().setAttribute("timePublicity", l.getTimePublicity());
			request.getSession().setAttribute("urlPublicity",l.getUrlPublicity());
			request.getSession().setAttribute("target",l.getHash());
			return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));

		}
		else{
			h.setLocation(URI.create(l.getTarget()));
			return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
		}
	}

	private Cookie findCookie(String value, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
		if(cookies == null){
			return null;
		} else {
			int i = 0;
			boolean found= false;
			while(!found && i<cookies.length){
				if(cookies[i].getName().equals(value)){
					found = true;
				}
				i++;
			}
			if(found){
				return cookies[i-1];
			} else {
				return null;
			}
		}
    }

	private ShortURL createAndSaveIfValid(String url, String sponsor, String urlPublicity, Integer timePublicity,
										  String owner, String ip, RedirectAttributes ra) {

		String id = Hashing.murmur3_32()
					.hashString(url, StandardCharsets.UTF_8).toString();
		ShortURL su;
		su = new ShortURL(id, url,
				linkTo(
						methodOn(UrlShortenerController.class).redirectTo(
								id, null,ra)).toUri(), sponsor, new Date(
				System.currentTimeMillis()), owner,
				HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null,timePublicity, urlPublicity,
                new Timestamp(Calendar.getInstance().getTime().getTime()),true,0);

		return shortURLRepository.save(su);

	}

	private boolean isReachable(String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			return responseCode == 200;
		} catch (IOException e) {
			return false;
		}
	}
}
