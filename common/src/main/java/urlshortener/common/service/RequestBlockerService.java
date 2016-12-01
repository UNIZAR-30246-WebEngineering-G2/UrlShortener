package urlshortener.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import urlshortener.common.domain.Click;
import urlshortener.common.domain.CoordinatesHelper;
import urlshortener.common.repository.ClickRepository;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service("RequestBlocker")
public class RequestBlockerService extends CoordinatesHelper{

    @Autowired private ClickRepository clickRepository;
    @Autowired private IPService ipService;

    private static final Logger logger = LoggerFactory.getLogger(RequestBlockerService.class);
    private final Integer SECONDS_FOR_REQUESTS = 10;

    public boolean tooMuchRequests(HttpServletRequest request, String hash){
        String ip = ipService.extractIP(request);

        ArrayList<String> locations = ipService.obtainLocation(ip);
        String lastClickLatitude = locations.get(0);
        String lastClickLongitude = locations.get(1);

        if(checkActiveBlocks(lastClickLatitude, lastClickLongitude)){
            logger.error("Detected blocked location, petition comes from (" + lastClickLatitude
                    + "," + lastClickLongitude + "), and current blocked location is: (" + blockedLatitude + ","
                    + blockedLongitude + ")");
            return true;
        }

        List<Click> previousClicks = clickRepository.findByHash(hash);

        SimpleDateFormat yearmonthday = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        SimpleDateFormat secondFormat = new SimpleDateFormat("ss");

        Timestamp lastClickTime = new Timestamp(System.currentTimeMillis());
        String lastClickDate = yearmonthday.format(lastClickTime);
        String lastClickHour = hourFormat.format(lastClickTime);
        String lastClickMinute = minuteFormat.format(lastClickTime);
        String lastClickSeconds = secondFormat.format(lastClickTime);

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

    private boolean checkActiveBlocks(String latitude, String longitude){
        if(blockedLatitude != null && blockedLongitude != null){
            String currentBlockedLatitude = blockedLatitude.toString();
            String currentBlockedLongitude = blockedLongitude.toString();

            return latitude.startsWith(currentBlockedLatitude) && longitude.startsWith(currentBlockedLongitude);
        } return false;
    }
}
