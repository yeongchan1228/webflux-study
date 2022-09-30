package reactivestudy.springreactivestudy.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by YC on 2022/09/25.
 * 다른 백 서버로 가정
 */
@SpringBootApplication
public class RemoteService {

    @RestController
    public static class RemoteController {
        @GetMapping("/remote-service")
        public String service1(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req + "/remote-service";
        }
    }

    @RestController
    public static class RemoteController2 {
        @GetMapping("/remote-service2")
        public String service2(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req + "/remote-service2";
        }
    }

    public static void main(String[] args) {
        System.setProperty("server.port", "9090");
        System.setProperty("server.tomcat.threads.max", "1000");
        SpringApplication.run(RemoteService.class, args);
    }
}
