package reactivestudy.springreactivestudy.reactive.async.v2;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Created by YC on 2022/09/25.
 */
@Service
@EnableAsync
public class MyServiceV2 {

    @Async(value = "myThreadPool")
    public ListenableFuture<String> work(String req) {
        return new AsyncResult<>(req + "/asyncWork");
    }

    @Bean
    ThreadPoolTaskExecutor myThreadPool() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(1);;
        te.setMaxPoolSize(1);
        te.initialize();

        return te;
    }
}
