package org.shaloman.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HolylifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HolylifeApplication.class, args);
	}

}
