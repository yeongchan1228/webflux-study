package reactivestudy.springreactivestudy.reactive.async.v4;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;

/**
 * Created by YC on 2022/09/28.
 */
@Slf4j
@RestController
@RequestMapping("/v4")
@SuppressWarnings("deprecation")
public class SpringAsyncControllerV4 {
    public static final String Service1Uri = "http://localhost:9090/remote-service?req={req}";
    public static final String Service2Uri = "http://localhost:9090/remote-service2?req={req}";
    private final AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

    // Netty를 사용하여 Thread 1개를 사용하도록 설정
    private final AsyncRestTemplate asyncRestTemplate2 = new AsyncRestTemplate(
            new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1))
    );

    private final MyServiceV4 myServiceV4;

    public SpringAsyncControllerV4(MyServiceV4 myServiceV4) {
        this.myServiceV4 = myServiceV4;
    }

    /**
     * 의존성이 존재하는 경우
     */
    @GetMapping("/rest4")
    public DeferredResult rest4(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();

        toCompletableFuture(asyncRestTemplate2.getForEntity(Service1Uri, String.class, "service1 " + idx))
                .thenCompose(res ->
                        toCompletableFuture(asyncRestTemplate.getForEntity(Service2Uri, String.class, res.getBody()))
                )
                .thenApplyAsync(res ->
                        myServiceV4.work(res.getBody())
                )
                .thenAccept(res -> dr.setResult(res))
                .exceptionally(ex -> { dr.setErrorResult(ex.getMessage()); return null; });

        return dr;
    }

    <T> CompletableFuture<T> toCompletableFuture(ListenableFuture<T> lf) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        lf.addCallback(
                res -> completableFuture.complete(res),
                ex -> completableFuture.completeExceptionally(ex)
        );
        return completableFuture;

    }
}
