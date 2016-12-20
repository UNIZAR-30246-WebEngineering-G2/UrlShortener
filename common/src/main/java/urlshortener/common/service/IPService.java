package urlshortener.common.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Service("IPService")
public interface IPService {

    ArrayList<String> obtainLocation(String ip);

    String extractIP(HttpServletRequest request);

}
