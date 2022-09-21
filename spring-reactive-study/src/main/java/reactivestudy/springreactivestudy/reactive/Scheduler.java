package reactivestudy.springreactivestudy.reactive;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created by YC on 2022/09/21.
 */
public class Scheduler {
    public static void main(String[] args) throws InterruptedException {

        // 1. 스레드 분리 -> publisher 별도의 스레드
        Flux.range(1, 10)
                .log()
                .subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(System.out::println);

        // 2. 스레드 분리 -> subscriber 별도의 스레드
        Flux.range(1, 10)
                .publishOn(Schedulers.newSingle("pub"))
                .log()
                .subscribe(System.out::println);

        // 3. 스레드 분리 -> publisher, subscriber 별도의 스레드 분리
        Flux.range(1, 10)
                .publishOn(Schedulers.newSingle("pub"))
                .log()
                .subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(System.out::println);

        // 4. interval() -> 시간 주기로 데이터 무한 생성, take() -> limit(제한) 후 종료
        Flux.interval(Duration.ofMillis(100))
                .take(10)
                .subscribe(System.out::println); // 데몬 스레드

        TimeUnit.SECONDS.sleep(5);
    }
}
