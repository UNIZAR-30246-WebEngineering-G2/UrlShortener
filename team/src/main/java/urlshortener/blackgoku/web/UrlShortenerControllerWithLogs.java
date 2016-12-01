package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.common.domain.MessageHelper;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.service.RequestBlockerService;
import urlshortener.common.web.UrlShortenerController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	@Autowired private RequestBlockerService requestBlockerService;

    @RequestMapping(value="/requestStatus", method = RequestMethod.GET)
	public void checkRequestStatus(@RequestParam("link")String id,
								   HttpServletRequest request, HttpServletResponse response){

		logger.info("Checking status to redirect " + id);

		try {
			PrintWriter out = response.getWriter();
			if(!requestBlockerService.tooMuchRequests(request, id)){
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

		if(!requestBlockerService.tooMuchRequests(request,id)){
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
}
