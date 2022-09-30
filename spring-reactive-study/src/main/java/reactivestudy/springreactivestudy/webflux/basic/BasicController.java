package reactivestudy.springreactivestudy.webflux.basic;

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
@RequestMapping
public class BasicController {
    public static final String Service1Uri = "http://localhost:9090/remote-service?req={req}";
    public static final String Service2Uri = "http://localhost:9090/remote-service2?req={req}";

    private final MyService myService;
    public BasicController(MyService myService) {
        this.myService = myService;
    }

    /**
     * 의존성이 존재하는 경우
     */
    @GetMapping("/rest")
    public DeferredResult rest4(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();

        toCompletableFuture(asyncRestTemplate2.getForEntity(Service1Uri, String.class, "service1 " + idx))
                .thenCompose(res ->
                        toCompletableFuture(asyncRestTemplate.getForEntity(Service2Uri, String.class, res.getBody()))
                )
                .thenApplyAsync(res ->
                        myService.work(res.getBody())
                )
                .thenAccept(res -> dr.setResult(res))
                .exceptionally(ex -> { dr.setErrorResult(ex.getMessage()); return null; });

        return dr;
    }

}
