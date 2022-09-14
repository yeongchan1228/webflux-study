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
        Publisher<Integer> mapPub = mapPub(pub, i -> i * 10);
//        Publisher<Integer> mapPub2 = mapPub(mapPub, i -> -i);
        mapPub.subscribe(printSubscribe());

        // 2. reduce -> 다른 개수
//        Publisher<Integer> reducePub = reducePub(pub, 0, (a, b) -> a + b);
//        reducePub.subscribe(printSubscribe());

    }

    private static Publisher<Integer> reducePub(Publisher<Integer> pub, int init, BiFunction<Integer, Integer, Integer> bf) {
        return sub -> {
            pub.subscribe(new DelegateSub(sub) {
                int result = init;

                @Override
                public void onNext(Integer item) {
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

    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> function) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new DelegateSub(sub) {
                    @Override
                    public void onNext(Integer item) {
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

    private static Subscriber<Integer> printSubscribe() {
        return new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
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
