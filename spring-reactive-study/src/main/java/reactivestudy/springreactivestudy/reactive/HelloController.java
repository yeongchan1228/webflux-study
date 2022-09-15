package reactivestudy.springreactivestudy.reactive;

import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Created by YC on 2022/09/15.
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public Publisher<String> hello(String name) {
        return Flux.just("Hello " + name);
    }
}
