package urlshortener.common.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service("CookieService")
public class CookieService {

    public Cookie findCookie(String value, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        } else {
            int i = 0;
            boolean found= false;
            while(!found && i<cookies.length){
                if(cookies[i].getName().equals(value)){
                    found = true;
                }
                i++;
            }
            if(found){
                return cookies[i-1];
            } else {
                return null;
            }
        }
    }
}
