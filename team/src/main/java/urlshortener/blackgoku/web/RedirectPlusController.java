package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.common.domain.*;
import urlshortener.blackgoku.domain.*;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class RedirectPlusController{

    private static final Logger logger = LoggerFactory.getLogger(RedirectPlusController.class);
    @Autowired
    protected ShortURLRepository shortURLRepository;
    @Autowired
    protected ClickRepository clickRepository;


    @RequestMapping(value = "/{id:(?!link|index).*}+", method = RequestMethod.GET)
    public Object redirectToPlus(@PathVariable String id,
                                       @RequestHeader(value="accept") String accept,
                                       HttpServletRequest request,
                                       RedirectAttributes ra) {
        ShortURL su = shortURLRepository.findByKey(id);

        String user = (String) request.getSession().getAttribute("user");
        //logger.info(accept);
        if(user != null){
            if(su != null){
                if(user.equals(su.getOwner())){
                    logger.info("Owner of the shortened URL requests more info");
                    if(accept.contains("html")){
                        logger.info("Html petition");
                        return new ModelAndView("redirect:/" + id + "+.html");
                    } else{
                        logger.info("JSON petition");
                        return new PlusObject(user,su.getCreated().toString(),su.getTarget(),request.getRemoteAddr(),
                                clickRepository.findByHash(su.getHash()).size(),
                                uniqueVisitors(clickRepository.findByHash(su.getHash())));
                    }
                } else{
                    logger.error("Someone who isn't the owner of the URL requested more info");
                    MessageHelper.addErrorAttribute(ra,"error.urlshortenerplus.owner",id);
                    return new ModelAndView("redirect:/");
                }
            } else{
                logger.error("Hash not found when asked for shortener plus");
                MessageHelper.addErrorAttribute(ra,"error.urlshortenerplus.notfound",id);
                return new ModelAndView("redirect:/");
            }
        } else{
            logger.error("Someone who isn't logged tried to request more info");
            MessageHelper.addErrorAttribute(ra,"error.logged.other",id);
            return new ModelAndView("redirect:/");
        }
    }

    @RequestMapping(value = "/{id:(?!link|index).*}+.html", method = RequestMethod.GET)
    public Object redirectToPlusHtml(@PathVariable String id,
                                 HttpServletRequest request,
                                 RedirectAttributes ra) {
        ShortURL su = shortURLRepository.findByKey(id);

        logger.info("Requested petition to return html with more info");

        String user = (String) request.getSession().getAttribute("user");
        if(user != null){
            if(su != null){
                if(user.equals(su.getOwner())){
                    ModelAndView response = new ModelAndView("infoUrl");
                    response.addObject("urlCreator",user);
                    response.addObject("numberClicks",clickRepository.findByHash(su.getHash()).size());
                    response.addObject("creationDate",su.getCreated());
                    response.addObject("targetUrl",su.getTarget());
                    response.addObject("uniqueVisitors",uniqueVisitors(clickRepository.findByHash(su.getHash())));
                    response.addObject("ownerIP", request.getRemoteAddr());
                    response.addObject("ownerIP",request.getRemoteAddr());
                    return response;
                } else{
                    MessageHelper.addErrorAttribute(ra,"error.urlshortenerplus.owner",id);
                    return new ModelAndView("redirect:/");
                }
            } else{
                MessageHelper.addErrorAttribute(ra,"error.urlshortenerplus.notfound",id);
                return new ModelAndView("redirect:/");
            }
        } else{
            MessageHelper.addErrorAttribute(ra,"error.logged.other",id);
            return new ModelAndView("redirect:/");
        }
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
