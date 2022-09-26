package reactivestudy.springreactivestudy.reactive.async.v3;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import reactivestudy.springreactivestudy.reactive.async.v2.MyServiceV2;

/**
 * Created by YC on 2022/09/25.
 */
@Slf4j
@RestController
@RequestMapping("/v3")
@SuppressWarnings("deprecation")
public class SpringAsyncControllerV3 {
    public static final String Service1Uri = "http://localhost:9090/remote-service?req={req}";
    public static final String Service2Uri = "http://localhost:9090/remote-service2?req={req}";
    private final AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

    // Netty를 사용하여 Thread 1개를 사용하도록 설정
    private final AsyncRestTemplate asyncRestTemplate2 = new AsyncRestTemplate(
            new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1))
    );

    private final MyServiceV2 myServiceV2;

    public SpringAsyncControllerV3(MyServiceV2 myServiceV2) {
        this.myServiceV2 = myServiceV2;
    }

    /**
     * 의존성이 존재하는 경우
     */
    @GetMapping("/rest4")
    public DeferredResult rest4(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();

        Completion
                .from(asyncRestTemplate2.getForEntity(Service1Uri, String.class, "service1 " + idx))
                .andApply(res -> asyncRestTemplate.getForEntity(Service2Uri, String.class, res.getBody()))
                .andApply(res -> myServiceV2.work(res.getBody()))
                .andError(ex -> dr.setErrorResult(ex.toString()))
                .andAccept(res -> dr.setResult(res));

        return dr;
    }
}
