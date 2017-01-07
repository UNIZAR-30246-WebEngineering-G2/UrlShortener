package urlshortener.blackgoku.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.scheduling.annotation.EnableScheduling;
import urlshortener.blackgoku.repository.UserRepository;
import urlshortener.blackgoku.repository.UserRepositoryImpl;
import urlshortener.common.repository.*;

@Configuration
@EnableScheduling
public class PersistenceContext {

	@Autowired
    protected JdbcTemplate jdbc;

	@Bean
	ShortURLRepository shortURLRepository() {
		return new ShortURLRepositoryImpl(jdbc);
	}
 	
	@Bean
	ClickRepository clickRepository() {
		return new ClickRepositoryImpl(jdbc);
	}

	@Bean
	UserRepository userRepository() {
		return new UserRepositoryImpl(jdbc);
	}

	@Bean
	ShortUrlStatsRepository shortUrlStatsRepository() {
		return new ShortUrlStatsRepositoryImpl(jdbc);
	}
}
