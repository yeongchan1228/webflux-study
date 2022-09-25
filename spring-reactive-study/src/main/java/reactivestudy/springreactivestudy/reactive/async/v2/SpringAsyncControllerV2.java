package reactivestudy.springreactivestudy.reactive.async.v2;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by YC on 2022/09/25.
 */
@Slf4j
@RestController
@RequestMapping("/v2")
@SuppressWarnings("deprecation")
public class SpringAsyncControllerV2 {
    public static final String Service1Uri = "http://localhost:9090/remote-service?req={req}";
    public static final String Service2Uri = "http://localhost:9090/remote-service2?req={req}";
    private final AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

    // Netty를 사용하여 Thread 1개를 사용하도록 설정
    private final AsyncRestTemplate asyncRestTemplate2 = new AsyncRestTemplate(
            new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1))
    );

    private final MyServiceV2 myServiceV2;

    public SpringAsyncControllerV2(MyServiceV2 myServiceV2) {
        this.myServiceV2 = myServiceV2;
    }

    static AtomicInteger counter = new AtomicInteger(0);

    /**
     * AsyncRestTemplate
     * 동시 요청 수 만큼 Thread를 추가 생성한다.
     */
    @GetMapping("/rest1")
    public ListenableFuture<ResponseEntity<String>> rest(int idx) {
        ListenableFuture<ResponseEntity<String>> res = asyncRestTemplate.getForEntity(
                Service1Uri, String.class, "hello " + idx
        );
        return res;
    }

    /**
     * HTTP Client Library Netty 사용
     * 최소한의 Thread 사용 -> 1개로 설정
     */
    @GetMapping("/rest2")
    public ListenableFuture<ResponseEntity<String>> rest2(int idx) {
        ListenableFuture<ResponseEntity<String>> res = asyncRestTemplate2.getForEntity(
                Service1Uri, String.class, "hello " + idx
        );
        return res;
    }

    /**
     * 응답 받은 데이터 가공
     */
    @GetMapping("/rest3")
    public DeferredResult rest3(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();

        ListenableFuture<ResponseEntity<String>> res = asyncRestTemplate2.getForEntity(
                Service1Uri, String.class, "hello " + idx
        );

//        res.get(); // 호출하는 순간 Block으로 호출한 의미가 사라진다.
        res.addCallback(data -> {
            dr.setResult(data.getBody() + " something add");
        }, ex -> {
//            throw new ex; Stack Trace를 모르기 때문에 전파하면 안 된다.
            dr.setErrorResult(ex);
        });

        return dr;
    }

    /**
     * 의존성이 존재하는 경우
     */
    @GetMapping("/rest4")
    public DeferredResult rest4(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();

        ListenableFuture<ResponseEntity<String>> future = asyncRestTemplate2.getForEntity(
                Service1Uri, String.class, "service1 " + idx
        );

//        res.get(); // 호출하는 순간 Block으로 호출한 의미가 사라진다.
        future.addCallback(data -> {
            ListenableFuture<ResponseEntity<String>> future2 = asyncRestTemplate.getForEntity(
                    Service2Uri, String.class, data.getBody()
            );

//            try {
//                ResponseEntity<String> result = res2.get();
//                dr.setResult(result.getBody());
//
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } catch (ExecutionException e) {
//                throw new RuntimeException(e);
//            } -> Block으로 응답 4, 6, 8, 10, 12... 로 모든 응답까지 총 202초 소요된다.

            future2.addCallback(data2 -> {
                ListenableFuture<String> future3 = myServiceV2.work(data.getBody());
                future3.addCallback(
                        data3 -> dr.setResult(data3),
                        ex -> dr.setErrorResult(ex)
                );
            }, ex -> {
                dr.setErrorResult(ex);
            }); // Callback 방식으로 Thread가 바로 반환되므로 총 4초 걸린다.
        }, ex -> {
//            throw new ex; Stack Trace를 모르기 때문에 전파하면 안 된다.
            dr.setErrorResult(ex);
        });

        return dr;
    }
}
