package reactivestudy.springreactivestudy.reactive.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by YC on 2022/09/25.
 */
@Slf4j
public class LoadTest {

    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate restTemplate= new RestTemplate();
        String url = "http://localhost:8080/v4/rest4?idx={idx}";

        CyclicBarrier barrier = new CyclicBarrier(101); // 스레드 동기화 기능 제공

        StopWatch main = new StopWatch();
        main.start();

        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                int idx = counter.addAndGet(1);

                barrier.await(); // Blocking -> .await()을 만난 스레드의 수가 위의 지정한 숫자(101)과 같아질 때까지 Blocking
                log.info("Thread {}", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                String res = restTemplate.getForObject(url, String.class, idx);

                sw.stop();
                log.info("Elapsed {} {} -> {}", idx, sw.getTotalTimeSeconds(), res);

                return null;
            });
        }

        barrier.await();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total time = {}", main.getTotalTimeSeconds());
    }

}
