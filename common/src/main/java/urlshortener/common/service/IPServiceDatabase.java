package urlshortener.common.service;

import com.maxmind.geoip2.record.*;
import org.springframework.stereotype.Service;

@Service("IPServiceDatabase")
public interface IPServiceDatabase {

    Location obtainLocation(String ip);

}
