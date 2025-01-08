package com.eventsnap.face;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.eventsnap.face"})
public class EventSnapApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventSnapApplication.class, args);
	}

}
