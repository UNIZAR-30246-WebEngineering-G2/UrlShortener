package urlshortener.blackgoku.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import urlshortener.common.service.CookieService;
import urlshortener.common.service.IPService;
import urlshortener.common.service.RequestBlockerService;
import urlshortener.common.web.CheckUrls;

@Configuration
public class BeanConfig {

    @Bean
    CheckUrls checkUrls(){
            return new CheckUrls();
    }

    @Bean
    RequestBlockerService requestBlocker(){
        return new RequestBlockerService();
    }

    @Bean
    IPService ipHelper(){
        return new IPService();
    }

    @Bean
    CookieService cookieService(){
        return new CookieService();
    }
}
