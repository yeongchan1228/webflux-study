package reactive.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.Flow.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by YC on 2022/09/21.
 */
public class Interval {
    public static void main(String[] args) {
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                int num = 0;
                boolean cancelled = false;

                @Override
                public void request(long n) {
                    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                    exec.scheduleAtFixedRate(() -> {
                        if (cancelled) {
                            exec.shutdown();
                            return;
                        }

                        sub.onNext(num++);
                    }, 0, 300, TimeUnit.MILLISECONDS);
                }

                @Override
                public void cancel() {
                    cancelled = true;
                }
            });
        };

        Publisher<Integer> takePub = sub -> {
            pub.subscribe(new Subscriber<Integer>() {
                int count = 0;
                Subscription subscription;

                @Override
                public void onSubscribe(Subscription subscription) {
                    this.subscription = subscription;
                    sub.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer integer) {
                    sub.onNext(integer);
                    if (++count >= 10) {
                        subscription.cancel();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    sub.onError(throwable);
                }

                @Override
                public void onComplete() {
                    sub.onComplete();
                }
            });
        };

        takePub.subscribe(new Subscriber<>() {
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
    }
}
