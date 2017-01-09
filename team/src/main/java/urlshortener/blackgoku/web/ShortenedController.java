package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;

@RestController
public class ShortenedController {

    private static final Logger logger = LoggerFactory.getLogger(ShortenedController.class);

    @Autowired
    private ShortURLRepository shortURLRepository;

    @RequestMapping(value = "/shortened/{id:(?!link|index).*}", method = RequestMethod.GET)
    public ShortURL information(@PathVariable String id){
        logger.info("Detected petition to load information about the already shortened URL with id " + id);
        return shortURLRepository.findByKey(id);
    }
}
