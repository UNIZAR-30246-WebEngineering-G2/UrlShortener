package urlshortener.blackgoku.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import urlshortener.common.web.CheckUrls;

/**
 * Created by AsierHandball on 23/11/2016.
 */
@Configuration
public class CheckingConfig {

    @Bean
    CheckUrls checkUrls(){
        try {
            return new CheckUrls();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
