package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.common.domain.MessageHelper;
import urlshortener.common.domain.Click;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.web.UrlShortenerController;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	private final Integer SECONDS_FOR_REQUESTS = 10;

	@RequestMapping(value = "/{id:(?!link|index).*}+", method = RequestMethod.GET)
	public ModelAndView redirectToPlus(@PathVariable String id, HttpServletRequest request,
									   RedirectAttributes ra) {
		ShortURL su = shortURLRepository.findByKey(id);

		String user = (String) request.getSession().getAttribute("user");
		if(user.equals(su.getOwner())){
			logger.info("Owner of the shortened URL requests more info");
			request.getSession().setAttribute("urlCreator",user);
			request.getSession().setAttribute("numberClicks",clickRepository.findByHash(su.getHash()).size());
			request.getSession().setAttribute("creationDate",su.getCreated().toString());
			request.getSession().setAttribute("targetUrl",su.getTarget());
			request.getSession().setAttribute("uniqueVisitors",uniqueVisitors(clickRepository.findByHash(su.getHash())));
			request.getSession().setAttribute("ownerIP",request.getRemoteAddr());
			return new ModelAndView("redirect:/" + id + "/moreInfo");
		} else{
			logger.error("Someone who isn't the owner of the URL requested more info");
			MessageHelper.addErrorAttribute(ra,"error.urlshortenerplus.owner",id);
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/publicity")
	public ModelAndView loadPublicity(HttpServletRequest request){
		String idUrl = (String) request.getSession().getAttribute("urlPubli");

		logger.info("Requested new advertisement redirection for id " + idUrl);

		ModelAndView modelAndView = new ModelAndView("publicity");
		ShortURL l = shortURLRepository.findByKey(idUrl);

		request.getSession().setAttribute("redirectionPublicity", l.getTarget());
		return modelAndView;
	}

	@RequestMapping(value = "/obtainPublicityLink")
	public void loadPublicityContent(HttpServletRequest request, HttpServletResponse response){
		String redirection = (String) request.getSession().getAttribute("redirectionPublicity");

		logger.info("Requested new redirection after loading publicity to url " + redirection);

		try {
			PrintWriter out = response.getWriter();
			out.print(redirection);
		} catch (IOException e) {
			logger.error("Error when obtaining printwriter in /obtainPublicityLink");
		}
	}

	@RequestMapping(value="/requestStatus", method = RequestMethod.GET)
	public void checkRequestStatus(@RequestParam("link")String id,
								   HttpServletRequest request, HttpServletResponse response,
								   RedirectAttributes ra){

		logger.info("Checking status to redirect " + id);

		try {
			PrintWriter out = response.getWriter();
			if(!tooMuchRequests(new Timestamp(System.currentTimeMillis()), extractIP(request), id)){
				out.print("ok");
			} else {
				logger.error("Too much requests to hash " + id + " in the same location, throttling API calls");
				out.print("exceeded");
			}
		} catch (IOException e) {
			logger.error("Error when obtaining printwriter in /requestStatus");
		}
	}



	private boolean tooMuchRequests(Timestamp lastClick, String ip, String hash){
		List<Click> previousClicks = clickRepository.findByHash(hash);

		SimpleDateFormat yearmonthday = new SimpleDateFormat("yyyy:MM:dd");
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
		SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
		SimpleDateFormat secondFormat = new SimpleDateFormat("ss");

		String lastClickDate = yearmonthday.format(lastClick);
		String lastClickHour = hourFormat.format(lastClick);
		String lastClickMinute = minuteFormat.format(lastClick);
		String lastClickSeconds = secondFormat.format(lastClick);

		ArrayList<String> locations = obtainLocation(ip);
		String lastClickLatitude = locations.get(0);
		String lastClickLongitude = locations.get(1);

		Timestamp beforeLastClick = null;

		for(Click c: previousClicks){
			String currentClickLatitude = c.getLatitude();
			String currentClickLongitude = c.getLongitude();

			if(lastClickLatitude.equals(currentClickLatitude) && lastClickLongitude.equals(currentClickLongitude)){
				beforeLastClick = c.getCreated();
			}
		}

		if(beforeLastClick != null){
			String beforeLastClickDate = yearmonthday.format(beforeLastClick);
			String beforeLastClickHour = hourFormat.format(beforeLastClick);
			String beforeLastClickMinute = minuteFormat.format(beforeLastClick);
			String beforeLastClickSeconds = secondFormat.format(beforeLastClick);

			logger.debug("Last click date: " + lastClickDate + " " + lastClickHour + " " + lastClickMinute + " " + lastClickSeconds);
			logger.debug("Previous last click date: " + beforeLastClickDate + " " + beforeLastClickHour + " "
					+ beforeLastClickMinute + " " + beforeLastClickSeconds);

			if(lastClickDate.equals(beforeLastClickDate)){
				logger.debug("Both the last click and the previous one from the same location are from the same day");
				Integer aux1 = Integer.parseInt(lastClickSeconds);
				Integer aux2 = Integer.parseInt(beforeLastClickSeconds);

				return (lastClickHour.equals(beforeLastClickHour)) && (lastClickMinute.equals(beforeLastClickMinute))
						&& ((aux1 - aux2) <= SECONDS_FOR_REQUESTS);
			}else return false;

		} else return false;
	}

	@Override
	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request,
										RedirectAttributes ra) {
		logger.info("Requested redirection with hash " + id);
		return super.redirectTo(id, request, ra);
	}

	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
											  @RequestParam(value = "sponsor", required = false) String sponsor,
											  HttpServletRequest request, RedirectAttributes ra) {
		logger.info("Requested new short for uri " + url);
		return super.shortener(url, sponsor, request, ra);
	}

	private int uniqueVisitors(List<Click> visitantes){
		if(visitantes.size() != 0){
			int contador = 1;
			Click lastVisitor = visitantes.get(0);

			for (Click click: visitantes){
				if(!lastVisitor.getIp().equals(click.getIp())){
					//New unique visitor
					contador++;
					lastVisitor = click;
				}
			}
			return contador;
		} else return 0;
	}
}
