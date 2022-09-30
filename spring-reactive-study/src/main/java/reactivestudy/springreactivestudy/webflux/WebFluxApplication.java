package reactivestudy.springreactivestudy.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebFluxApplication {

	public static void main(String[] args) {
		System.setProperty("reactor.netty.ioWorkerCount", "2");
		SpringApplication.run(WebFluxApplication.class, args);
	}

}
