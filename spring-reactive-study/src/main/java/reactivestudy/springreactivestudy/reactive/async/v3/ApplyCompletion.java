package reactivestudy.springreactivestudy.reactive.async.v3;

import org.springframework.util.concurrent.ListenableFuture;

import java.util.function.Function;

/**
 * Created by YC on 2022/09/26.
 */
public class ApplyCompletion<S, T> extends Completion<S, T> {

    Function<S, ListenableFuture<T>> function;

    public ApplyCompletion(Function<S, ListenableFuture<T>> function) {
        this.function = function;
    }

    @Override
    public void run(S value) {
        ListenableFuture<T> lf = function.apply(value);
        lf.addCallback(res -> complete(res), ex -> error(ex));
    }
}
