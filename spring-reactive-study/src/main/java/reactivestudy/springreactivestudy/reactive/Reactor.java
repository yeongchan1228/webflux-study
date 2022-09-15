package reactivestudy.springreactivestudy.reactive;

import reactor.core.publisher.Flux;

/**
 * Created by YC on 2022/09/15.
 */
public class Reactor {
    public static void main(String[] args) {
        // 1
        Flux.create(e -> {
            e.next(1);
            e.next(2);
            e.next(3);
            e.complete();
        })
                .log()
                .subscribe(System.out::println);

        // 2
        Flux.just(1, 2, 3)
                .log()
                .map(item -> item * 2)
                .reduce(0, (a, b) -> a + b)
                .log()
                .subscribe(System.out::println);

    }
}
