package urlshortener.common.web;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.common.domain.CoordinatesHelper;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.domain.Click;
import urlshortener.common.service.CookieService;
import urlshortener.common.service.IPService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UrlShortenerController extends CoordinatesHelper {

	private static final Logger LOG = LoggerFactory.getLogger(UrlShortenerController.class);
	@Autowired private CheckUrls checkUrls;
	@Autowired private ShortURLRepository shortURLRepository;
	@Autowired private ClickRepository clickRepository;
	@Autowired private IPService ipService;
	@Autowired private CookieService cookieService;

	@RequestMapping(value = "/{id:(?!link).*}", method = RequestMethod.GET)
	public Object redirectTo(@PathVariable String id,
							 HttpServletRequest request,
							 RedirectAttributes ra) {
		ShortURL l = shortURLRepository.findByKey(id);
		if (l != null && l.getActive()) {
			createAndSaveClick(id, ipService.extractIP(request));
			return createSuccessfulRedirectToResponse(l, request, id);
		} else {
			if (l!=null) {
				request.getSession().setAttribute("UltimaVezEnPie", l.getLast_time_up());
				return new ModelAndView("urlDown.html");
			}else {
				LOG.error("Redirection petition with hash " + id + " not found");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}
	}

	private void createAndSaveClick(String hash, String ip) {
		ArrayList<String> locations = ipService.obtainLocation(ip);

		Click cl = new Click(null, hash, new Timestamp(System.currentTimeMillis()),
				null, null, null, ip, null,
				locations.size() == 0 ? "IP not in DB" : locations.get(0),
				locations.size() == 0 ? "IP not in DB" : locations.get(1));
		cl=clickRepository.save(cl);
		LOG.info(cl!=null?"["+hash+"] saved with id ["+cl.getId()+"]":"["+hash+"] was not saved");

	}

	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
											  @RequestParam(value = "sponsor", required = false) String sponsor,
											  @RequestParam(value = "publicity-url", required = false) String urlPublicity,
											  @RequestParam(value = "time-publicity", required = false) Integer timePublicity,
											  HttpServletRequest request, RedirectAttributes ra) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		if(urlValidator.isValid(url)){
				String id = (String) request.getSession().getAttribute("user");
				if(id == null) id="";
				ShortURL su = createAndSaveIfValid(url, sponsor, urlPublicity, timePublicity ,id, ipService.extractIP(request),ra);
				if (su != null) {
                    HttpHeaders h = new HttpHeaders();
					h.setLocation(su.getUri());
					if(!su.getOwner().equals(id)){
						//Shortened URL already exists
						LOG.error("Extended URL requested has already been shortened");
						return new ResponseEntity<>(su, h,HttpStatus.CONFLICT);
					} else{
                        boolean active = IPService.isReachable(url);
                        su.setActive(active);
                        if(checkUrls!= null) checkUrls.agnadirUrl(su);
                        su.setLastChange(new Timestamp(Calendar.getInstance().getTime().getTime()));
                        if(su.getActive()){
							su.setLast_time_up(new Timestamp(Calendar.getInstance().getTime().getTime()));
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

	private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l, HttpServletRequest request, String id) {
		boolean lessThanTimeEstablished = false;
        final int TIME_ESTABLISHED = 30;
        long time;
        HttpHeaders h = new HttpHeaders();
        Cookie click = cookieService.findCookie(id,request);
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
}
