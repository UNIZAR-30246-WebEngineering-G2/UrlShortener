package urlshortener.blackgoku.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import urlshortener.blackgoku.domain.Coordinates;
import urlshortener.common.domain.CoordinatesHelper;

@Controller
public class WebSocketEndpoint extends CoordinatesHelper{
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEndpoint.class);

    @MessageMapping("/ipcoord")
    @SendTo("/topic/answer")
    public Coordinates answeringToWebSocket(){
        double lat = 0.0;
        double lon =0.0;
        if (CoordinatesHelper.blockedLatitude!=null && CoordinatesHelper.blockedLongitude!=null) {
            lat = CoordinatesHelper.blockedLatitude;
            lon = CoordinatesHelper.blockedLongitude;
        }
       return new Coordinates(lat,lon);
    }

}
