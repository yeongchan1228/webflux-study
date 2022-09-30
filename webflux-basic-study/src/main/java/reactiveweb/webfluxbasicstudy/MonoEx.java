package reactiveweb.webfluxbasicstudy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Created by YC on 2022/09/30.
 */
@Slf4j
public class MonoEx {

    @RestController
    @RequestMapping("/mono")
    public static class MonoExampleController {
        @GetMapping("/1")
        public Mono<String> ex1() {
            log.info("first");
            Mono<String> mono = Mono.just("Hello WebFlux").log();
            log.info("second");
            return mono;
        }

        @GetMapping("/2")
        public Mono<String> ex2() {
            log.info("first");
            Mono<String> mono = Mono.just(generateHello()).log();
            log.info("second");
            return mono;
        }

        @GetMapping("/3")
        public Mono<String> ex3() {
            log.info("first");
            Mono<String> mono = Mono.fromSupplier(() -> generateHello()).log();
            log.info("second");
            return mono;
        }

        @GetMapping("/4")
        public Mono<String> ex4() {
            log.info("first");
            Mono<String> mono = Mono.fromCallable(() -> generateHello()).log();
            mono.subscribe();
            log.info("second");
            return mono;
        }

        private String generateHello() {
            log.info("method generateHello");
            return "Hello Mono";
        }
    }
}
