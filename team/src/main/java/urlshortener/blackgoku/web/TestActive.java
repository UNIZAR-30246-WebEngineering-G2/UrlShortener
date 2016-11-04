package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.ws.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
        URL urlServer = null;
        try {
            urlServer = new URL(this.url);
            logger.info("Trying to reach "+url);
            HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
            urlConn.setConnectTimeout(5000); //<- 5 Seconds Timeout
            urlConn.connect();
            logger.info("URL returns" +urlConn.getResponseCode());
            if (urlConn.getResponseCode() == 200) {		// URL reachable
                return true;
            } else {				// URL unreachable
                return false;
            }
        } catch (java.io.IOException e) {
            return false;
        }
    }

}
