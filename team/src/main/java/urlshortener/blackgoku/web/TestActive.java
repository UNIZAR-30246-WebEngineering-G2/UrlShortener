package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by AsierHandball on 04/11/2016.
 */
public class TestActive {
    private String url;
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

    public TestActive(String url){
        this.url=url;
    }
    public boolean isActive() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = restTemplate.getForEntity(url, String.class);
        int responseCode = responseEntity.getStatusCodeValue();
        if (responseCode == 200) {
            logger.info("200 OK returning the shortened uri");
            return true;
        } else {
            logger.info(responseCode + " error uri unreachable returning error to user.");
            return false;
        }
    }
}
