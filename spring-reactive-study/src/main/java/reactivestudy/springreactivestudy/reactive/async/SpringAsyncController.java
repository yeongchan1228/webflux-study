package reactivestudy.springreactivestudy.reactive.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by YC on 2022/09/24.
 */
@Slf4j
@RestController
public class SpringAsyncController {

    static AtomicInteger counter = new AtomicInteger(0);

    @GetMapping("/basic")
    public String async() throws InterruptedException {
        Thread.sleep(2000);
        return "basic";
    }

    @GetMapping("/callable")
    public Callable<String> callable() throws InterruptedException {
        log.info("callable");
        return () -> {
            log.info("async");
            Thread.sleep(2000);
            return "hello";
        };
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate restTemplate= new RestTemplate();
        String url = "http://localhost:8080/callable";

        StopWatch main = new StopWatch();
        main.start();

        for (int i = 0; i < 100; i++) {
            es.execute(() -> {
                int idx = counter.addAndGet(1);
                log.info("Thread {}", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                restTemplate.getForObject(url, String.class);

                sw.stop();
                log.info("Elapsed {} {}", idx, sw.getTotalTimeSeconds());

            });
        }

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total time = {}", main.getTotalTimeSeconds());
    }

}
