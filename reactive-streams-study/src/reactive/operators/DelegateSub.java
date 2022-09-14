package reactive.operators;


import java.util.concurrent.Flow;

import static java.util.concurrent.Flow.*;

/**
 * Created by YC on 2022/09/14.
 */
public class DelegateSub implements Subscriber<Integer> {

    Subscriber sub;

    public DelegateSub(Subscriber<? super Integer> sub) {
        this.sub = sub;
    }

    @Override
    public void onSubscribe(Subscription s) {
        sub.onSubscribe(s);
    }

    @Override
    public void onNext(Integer item) {
        sub.onNext(item);
    }

    @Override
    public void onError(Throwable t) {
        sub.onError(t);
    }

    @Override
    public void onComplete() {
        sub.onComplete();
    }
}
