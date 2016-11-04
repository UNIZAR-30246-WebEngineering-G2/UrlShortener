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

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.blackgoku.domain.MessageHelper;
import urlshortener.common.domain.Click;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.web.UrlShortenerController;

import java.util.List;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

	@Override
	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		logger.info("Requested redirection with hash " + id);
		return super.redirectTo(id, request);
	}

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

	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
											  @RequestParam(value = "sponsor", required = false) String sponsor,
											  HttpServletRequest request) {
		logger.info("Requested new short for uri " + url);
		return super.shortener(url, sponsor, request);
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
