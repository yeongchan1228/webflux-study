package reactivestudy.springreactivestudy.reactive.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Future;

/**
 * Created by YC on 2022/09/24.
 */
@Slf4j
//@Component
@EnableAsync
public class SpringAsyncEx {

    /**
     * ThreadPool 등록
     */
    @Bean
    ThreadPoolTaskExecutor tp() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();

        // 처음 스레드 요청이 들어오면 스레드 10개 생성 -> 11번 째 요청 시 대기 큐에 저장 -> 대기 큐가 200개로 꽉 참 -> maxPoolSize까지 스레드 풀을 늘린다.
        te.setCorePoolSize(10); // 스레드 10개 생성 -> 바로 만드는 것이 아닌, 첫 스레드 요청이오면 생성한다. 기본은 8
        te.setMaxPoolSize(100); // 대기 큐가 꽉 차면 그때 100개 까지 생성한다.
        te.setQueueCapacity(200); // 대기 큐 사이즈 : 200개 요청 대기 가능

        te.setKeepAliveSeconds(30); // 반환 받은 스레드가 30초 동안 사용되지 않으면 제거
//        te.setTaskDecorator(); // 스레드를 만들거나 반환하기 시작 전, 시작 후로 작업을 적용할 수 있다.
        te.setThreadNamePrefix("test"); // 스레드 이름 설정
        te.initialize(); // 초기화 반환 전 받드시 실행
        return te;
    }

    /**
     * Future 반환 -> .get() : blocking
     */
    @Async
    @Component
    public static class MyService1 {
        public Future<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(1000);
            return new AsyncResult<>("Hello");
        }

    }

    @Autowired MyService1 myService1;

    @Bean
    ApplicationRunner run1() {
        return args -> {
            log.info("run()");
            Future<String> res = myService1.hello();
            log.info("res = {}", res.get()); // 결과 값을 받기 전까지 Block
            log.info("exit");
        };
    }

    /**
     * ListenableFuture -> Callback : non-blocking
     */
    @Async(value = "tp")
    @Component
    public static class MyService2 {
        public ListenableFuture<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(1000);
            return new AsyncResult<>("Hello");
        }
    }

    @Autowired MyService2 myService2;

    @Bean
    ApplicationRunner run2() {
        return args -> {
            log.info("run()");
            ListenableFuture<String> res = myService2.hello();
            res.addCallback(data -> log.info("res = {}", data), err -> log.error("err =", err));
            log.info("exit");
        };
    }
}
