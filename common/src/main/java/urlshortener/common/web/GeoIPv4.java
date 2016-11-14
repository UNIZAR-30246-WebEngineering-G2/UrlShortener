package urlshortener.common.web;

import com.maxmind.geoip.LookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;

public class GeoIPv4 {

    private static final Logger logger = LoggerFactory.getLogger(GeoIPv4.class);
    private static LookupService lookUp;

    static {
        try {
            lookUp = new LookupService(
                    GeoIPv4.class.getResource("/location/GeoLiteCity.dat").getPath(),
                    LookupService.GEOIP_MEMORY_CACHE);
            //System.out.println("GeoIP Database loaded: " + lookUp.getDatabaseInfo());
        } catch (IOException e) {
            logger.error("Couldn't load GeoIP database");
        }
    }

    public static GeoLocation getLocation(String ipAddress) {
        return GeoLocation.map(lookUp.getLocation(ipAddress));
    }

    public static GeoLocation getLocation(long ipAddress){
        return GeoLocation.map(lookUp.getLocation(ipAddress));
    }

    public static GeoLocation getLocation(InetAddress ipAddress){
        return GeoLocation.map(lookUp.getLocation(ipAddress));
    }
}
