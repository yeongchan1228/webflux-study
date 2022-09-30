package reactivestudy.springreactivestudy.webflux.basic;

import org.springframework.stereotype.Service;

/**
 * Created by YC on 2022/09/25.
 */
@Service
public class MyService {

    public String work(String req) {
        return req + "/asyncWork";
    }

}
