package reactiveweb.webfluxbasicstudy.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Created by YC on 2022/09/28.
 */
@Slf4j
@RestController
public class BasicController {
    public static final String Service1Uri = "http://localhost:9090/remote-service?req={req}";
    public static final String Service2Uri = "http://localhost:9090/remote-service2?req={req}";

    private final MyService myService;
    private final WebClient webClient = WebClient.create();

    public BasicController(MyService myService) {
        this.myService = myService;
    }

    /**
     * 의존성이 존재하는 경우
     */
    @GetMapping("/rest")
    public Mono<String> rest4(@RequestParam(required = false) int idx) {
        Mono<String> result = webClient
                .get()
                .uri(Service1Uri, idx)
                .retrieve()
                .bodyToMono(String.class);

        result.subscribe((res) -> System.out.println("res = " + res));

        return Mono.just("Hello");
    }

    @Service
    public static class MyService {
        public String work(String req) {
            return req + "/asyncWork";
        }
    }
}
