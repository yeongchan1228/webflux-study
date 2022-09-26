package reactivestudy.springreactivestudy.reactive.async.v3;

import java.util.function.Consumer;

/**
 * Created by YC on 2022/09/26.
 */
public class AcceptCompletion<S> extends Completion<S, Void> {

    Consumer<S> consumer;

    public AcceptCompletion(Consumer<S> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run(S value) {
        consumer.accept(value);
    }
}
