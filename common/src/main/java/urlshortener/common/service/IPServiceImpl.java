package urlshortener.common.service;

import com.maxmind.geoip2.record.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@Service("IPService")
public class IPServiceImpl implements IPService{

    private IPServiceDatabaseImpl ipServiceDb = new IPServiceDatabaseImpl();
    private static final Logger logger = LoggerFactory.getLogger(IPServiceImpl.class);

    public ArrayList<String> obtainLocation(String ip){
        String latitude = "IP not in DB";
        String longitude = "IP not in DB";

        Location clientLocation = ipServiceDb.obtainLocation(ip);

        if(clientLocation != null){
            latitude = String.valueOf(clientLocation.getLatitude());
            longitude = String.valueOf(clientLocation.getLongitude());

            logger.info("Latitud of new visitor: " + latitude);
            logger.info("Longitude of new visitor: " + longitude);

        } else logger.info("Information about IP " + ip + " not found");

        ArrayList<String> locationArray = new ArrayList<>();
        locationArray.add(latitude);
        locationArray.add(longitude);

        return locationArray;
    }

    public String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public static boolean isReachable(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (IOException e) {
            logger.error("Error when trying to check if " + url + " is reachable or not");
            return false;
        }
    }
}
