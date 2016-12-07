package urlshortener.common.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service("IPServiceDatabase")
public class IPServiceDatabase {

    private DatabaseReader reader;

    public IPServiceDatabase(){
        try {
            reader = new DatabaseReader.Builder(getClass().getResourceAsStream("/location/GeoLite2-City.mmdb")).build();
        } catch (IOException e) {
            reader=null;
        }
    }

    public Location obtainLocation(String ip){

        try {
            InetAddress ipAddress = InetAddress.getByName(ip);

            CityResponse response = reader.city(ipAddress);

            return response.getLocation();

        } catch (IOException | GeoIp2Exception e) {
            return null;
        }
    }

}
