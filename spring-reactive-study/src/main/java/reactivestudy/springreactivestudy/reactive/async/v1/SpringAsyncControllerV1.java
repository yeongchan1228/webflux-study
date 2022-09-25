package reactivestudy.springreactivestudy.reactive.async.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by YC on 2022/09/24.
 */
@Slf4j
@RestController
public class SpringAsyncControllerV1 {

    static AtomicInteger counter = new AtomicInteger(0);
    Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();

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

    @GetMapping("/dr")
    public DeferredResult<String> dr() throws InterruptedException {
        log.info("dr");
        DeferredResult<String> dr = new DeferredResult<>();
        results.add(dr);
        return dr;
    }

    @GetMapping("/dr/count")
    public String drCount() {
        return String.valueOf(results.size());
    }

    @GetMapping("/dr/event")
    public String drEvent(String msg) {
        for (DeferredResult<String> dr : results) {
            dr.setResult("DrResult " + msg);
            results.remove(dr);
        }

        return "OK";
    }

    @GetMapping("/emitter")
    public ResponseBodyEmitter emitter(String msg) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        Executors.newSingleThreadExecutor().submit(() -> {
            for (int i = 0; i < 51; i++) {
                try {
                    emitter.send("<p> Stream " + i + "<p/>");
                    Thread.sleep(500);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return emitter;
    }
}
