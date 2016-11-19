package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.common.domain.Click;
import urlshortener.common.domain.MessageHelper;
import urlshortener.common.domain.ShortURL;
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
    public ModelAndView redirectToPlus(@PathVariable String id, HttpServletRequest request,
                                       RedirectAttributes ra) {
        ShortURL su = shortURLRepository.findByKey(id);

        String user = (String) request.getSession().getAttribute("user");
        if(user != null){
            if(su != null){
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
