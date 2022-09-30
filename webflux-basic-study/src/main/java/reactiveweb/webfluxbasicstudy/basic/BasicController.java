package reactiveweb.webfluxbasicstudy.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

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

    @GetMapping("/rest")
    public Mono<String> rest4(@RequestParam(required = false) int idx) {
        return webClient
                .get().uri(Service1Uri, idx).retrieve().bodyToMono(String.class)
                .flatMap(res -> webClient.get().uri(Service2Uri, res).retrieve().bodyToMono(String.class))
                .doOnNext(res -> log.info(res))
                .flatMap(res -> Mono.fromCompletionStage(myService.work(res)))
                .doOnNext(res -> log.info(res));
    }

    @Service
    public static class MyService {
        @Async
        public CompletableFuture<String> work(String req) {
            return CompletableFuture
                    .completedFuture(req + "/asyncWork");
        }
    }

    @Bean
    ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setThreadNamePrefix("myThread");

        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
