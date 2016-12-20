package urlshortener.common.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service("CookieService")
public interface CookieService {

    Cookie findCookie(String value, HttpServletRequest request);

}
