package urlshortener.blackgoku;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import urlshortener.blackgoku.domain.User;
import urlshortener.blackgoku.repository.UserRepository;

@SpringBootApplication
public class Application extends SpringBootServletInitializer  implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;


	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	@Override
	public void run(String... args) throws Exception {
		userRepository.save(new User("admin@admin.com","admin"));
	}

}