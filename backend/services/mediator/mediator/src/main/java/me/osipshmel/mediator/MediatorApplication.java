package me.osipshmel.mediator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "me.osipshmel.mediator.repository")
public class MediatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediatorApplication.class, args);
	}

}
