package urlshortener.common.web;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Fran Menendez Moya on 14/11/2016.
 */
public class IPv2 {


    public Location obtainLocation(String ip){

        // This creates the DatabaseReader object, which should be reused across
        // lookups.
        DatabaseReader reader = null;
        try {
            reader = new DatabaseReader.Builder(getClass().getResourceAsStream("/location/GeoLite2-City.mmdb")).build();

            InetAddress ipAddress = InetAddress.getByName(ip);

            // Replace "city" with the appropriate method for your database, e.g.,
            // "country".
            CityResponse response = reader.city(ipAddress);

            return response.getLocation();

        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
