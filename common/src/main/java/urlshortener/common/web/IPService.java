package urlshortener.common.web;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;

import java.io.IOException;
import java.net.InetAddress;


public class IPService {

    private DatabaseReader reader;

    public IPService(){
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
