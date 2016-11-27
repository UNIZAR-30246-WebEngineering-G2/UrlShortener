package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.common.domain.Click;
import urlshortener.common.domain.MessageHelper;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.web.CheckUrls;
import urlshortener.common.web.UrlShortenerController;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping(value="/requestStatus", method = RequestMethod.GET)
	public void checkRequestStatus(@RequestParam("link")String id,
								   HttpServletRequest request, HttpServletResponse response){

		logger.info("Checking status to redirect " + id);

		try {
			PrintWriter out = response.getWriter();
			if(!tooMuchRequests(request, id)){
				out.print("ok");
			} else {
				logger.error("Too much requests to hash " + id + " in the same location, throttling API calls");
				out.print("exceeded");
			}
		} catch (IOException e) {
			logger.error("Error when obtaining printwriter in /requestStatus");
		}
	}

	@Override
	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	public Object redirectTo(@PathVariable String id, HttpServletRequest request,
										RedirectAttributes ra) {
		logger.info("Requested redirection with hash " + id);

		if(!tooMuchRequests(request,id)){
			return super.redirectTo(id, request, ra);
		} else{
			logger.error("Too much requests found for " + id + " from the same location");
			MessageHelper.addErrorAttribute(ra,"error.toomuchrequests","");
			return new ModelAndView("redirect:/");
		}

	}

	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
											  @RequestParam(value = "sponsor", required = false) String sponsor,
											  @RequestParam(value = "publicity-url", required = false) String urlPublicity,
											  @RequestParam(value = "time-publicity", required = false) Integer timePublicity,
											  HttpServletRequest request, RedirectAttributes ra) {
        logger.info("Requested new short for uri " + url);
		return super.shortener(url, sponsor, urlPublicity, timePublicity, request, ra);
	}

	private boolean tooMuchRequests(HttpServletRequest request, String hash){
		String ip = extractIP(request);
		List<Click> previousClicks = clickRepository.findByHash(hash);

		SimpleDateFormat yearmonthday = new SimpleDateFormat("yyyy:MM:dd");
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
		SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
		SimpleDateFormat secondFormat = new SimpleDateFormat("ss");

		Timestamp lastClick = new Timestamp(System.currentTimeMillis());
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
}
