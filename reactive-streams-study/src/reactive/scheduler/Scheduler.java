package reactive.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

import static java.util.concurrent.Flow.*;

/**
 * Created by YC on 2022/09/17.
 */

public class Scheduler {

    public static void main(String[] args) {
        Publisher pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    System.out.println(Thread.currentThread().getName() + " [Request]");
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onNext(4);
                    sub.onNext(5);
                    sub.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };

        // SubscribeOn
        Publisher<Integer> subOnPub = s -> {
            ExecutorService es = Executors.newSingleThreadExecutor();
            es.execute(() -> pub.subscribe(s));
            es.shutdown(); // 실행 중인 스레드 전부 실행 후 종료
        };

        // PublishOn
        Publisher<Integer> pubOnPub = s -> {
            subOnPub.subscribe(new Subscriber<Integer>() {
                ExecutorService es = Executors.newSingleThreadExecutor();
                @Override
                public void onSubscribe(Subscription subscription) {
                    s.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer integer) {
                    es.execute(() -> s.onNext(integer));
                }

                @Override
                public void onError(Throwable throwable) {
                    es.execute(() -> s.onError(throwable));
                    es.shutdown();
                }

                @Override
                public void onComplete() {
                    es.execute(() -> s.onComplete());
                    es.shutdown();
                }
            });
        };

        pubOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println(Thread.currentThread().getName() + " [onSubscribe]");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println(Thread.currentThread().getName() + " [onNext] data = " + integer);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(Thread.currentThread().getName() + " [onError] ");
            }

            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread().getName() + " [onComplete]");
            }
        });

        System.out.println(Thread.currentThread().getName() + " exit");
    }


}
