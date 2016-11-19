package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.common.domain.MessageHelper;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
public class PublicityController{

    private static final Logger logger = LoggerFactory.getLogger(PublicityController.class);
    @Autowired
    protected ShortURLRepository shortURLRepository;

    @RequestMapping(value = "/publicity", method = RequestMethod.GET)
    public ModelAndView loadPublicity(HttpServletRequest request, RedirectAttributes ra){
        String idUrl = (String) request.getSession().getAttribute("target");

        if(idUrl != null){
            logger.info("Requested new advertisement redirection for id " + idUrl);

            ModelAndView modelAndView = new ModelAndView("publicity");
            ShortURL l = shortURLRepository.findByKey(idUrl);

            request.getSession().setAttribute("redirectionPublicity", l.getTarget());
            return modelAndView;

        } else{
            logger.error("No link hash with advertisement enabled was found in session");
            MessageHelper.addErrorAttribute(ra,"error.advertisement.url.notfound","");
            return new ModelAndView("redirect:/");
        }
    }

    @RequestMapping(value = "/obtainPublicityLink", method = RequestMethod.GET)
    public void loadPublicityContent(HttpServletRequest request, HttpServletResponse response){
        String redirection = (String) request.getSession().getAttribute("redirectionPublicity");

        logger.info("Requested new redirection after loading publicity to url " + redirection);

        try {
            PrintWriter out = response.getWriter();

            if(redirection != null){
                out.print(redirection);
            } else out.print("Not found");
        } catch (IOException e) {
            logger.error("Error when obtaining printwriter in /obtainPublicityLink");
        }
    }
}
