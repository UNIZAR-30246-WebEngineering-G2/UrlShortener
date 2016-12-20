package urlshortener.common.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service("RequestBlocker")
public interface RequestBlockerService {

    boolean tooMuchRequests(HttpServletRequest request, String hash);
}
