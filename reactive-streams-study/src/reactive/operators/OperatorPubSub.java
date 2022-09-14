package reactive.operators;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.Flow.*;

/**
 * Created by YC on 2022/09/14.
 */
public class OperatorPubSub {

    public static void main(String[] args) {
        Publisher<Integer> pub = iterPublisher(Stream.iterate(1, i -> i + 1).limit(10).collect(Collectors.toList()));

        // 1. Map -> 동일 개수
        Publisher<String> mapPub = mapPub(pub, i -> "[" + i + "]");
        mapPub.subscribe(printSubscribe());

        // 2. reduce -> 다른 개수
        Publisher<String> reducePub = reducePub(pub, "", (a, b) -> a + b);
        reducePub.subscribe(printSubscribe());

    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return sub -> {
            pub.subscribe(new DelegateSub<T, R>(sub) {
                R result = init;

                @Override
                public void onNext(T item) {
                    result = bf.apply(this.result, item);
                }

                @Override
                public void onComplete() {
                    sub.onNext(result);
                    sub.onComplete();
                }
            });
        };
    }

    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> function) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSub<T, R>(sub) {
                    @Override
                    public void onNext(T item) {
                        sub.onNext(function.apply(item));
                    }
                });
            }
        };
    }

    private static Publisher<Integer> iterPublisher(Iterable<Integer> iter) {
        return new Publisher<>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iter.forEach(num -> sub.onNext(num));
                            sub.onComplete();
                        } catch (Throwable t) {
                            sub.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }

    private static <T> Subscriber<T> printSubscribe() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T item) {
                System.out.println("[onNext] item = " + item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("[onError] throwable = " + throwable);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
    }

}
