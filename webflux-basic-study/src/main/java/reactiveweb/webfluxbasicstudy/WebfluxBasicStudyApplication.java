package reactiveweb.webfluxbasicstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class WebfluxBasicStudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxBasicStudyApplication.class, args);
	}

}
