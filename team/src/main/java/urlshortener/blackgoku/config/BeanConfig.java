package urlshortener.blackgoku.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import urlshortener.common.service.CookieServiceImpl;
import urlshortener.common.service.IPServiceImpl;
import urlshortener.common.service.RequestBlockerServiceImpl;
import urlshortener.common.web.CheckUrls;

@Configuration
public class BeanConfig {

    @Bean
    CheckUrls checkUrls(){
            return new CheckUrls();
    }

    @Bean
    RequestBlockerServiceImpl requestBlocker(){
        return new RequestBlockerServiceImpl();
    }

    @Bean
    IPServiceImpl ipHelper(){
        return new IPServiceImpl();
    }

    @Bean
    CookieServiceImpl cookieService(){
        return new CookieServiceImpl();
    }
}
