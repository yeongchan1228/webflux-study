package reactive.streams;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.Flow.*;

/**
 * Created by YC on 2022/09/06.
 */
public class PubSub {
    public static void main(String[] args) {
        Iterable<Integer> iter = List.of(1, 2, 3, 4, 5);
        ExecutorService es = Executors.newSingleThreadExecutor();

        Publisher p = new Publisher() {
            @Override
            public void subscribe(Subscriber subscriber) {
                Iterator<Integer> it = iter.iterator();

                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        es.execute(() -> {
                            int i = 0;
                            try {
                                while (i++ < n) {
                                    if (it.hasNext()) {
                                        subscriber.onNext(it.next());
                                    } else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                subscriber.onError(e);
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> s = new Subscriber<>() {
            Subscription subscription;
            final int MAX = 1;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println(Thread.currentThread().getName() + " onSubscribe");

                this.subscription = subscription;
                subscription.request(MAX);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println(Thread.currentThread().getName() + " onNext -> " + item);

                this.subscription.request(MAX);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError" + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        p.subscribe(s);

        try {
            es.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            es.shutdown();
        }
    }
}
