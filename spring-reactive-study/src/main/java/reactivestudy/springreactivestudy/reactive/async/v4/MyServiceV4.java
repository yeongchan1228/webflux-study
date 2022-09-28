package reactivestudy.springreactivestudy.reactive.async.v4;

import org.springframework.stereotype.Service;

/**
 * Created by YC on 2022/09/25.
 */
@Service
public class MyServiceV4 {

    public String work(String req) {
        return req + "/asyncWork";
    }

}
