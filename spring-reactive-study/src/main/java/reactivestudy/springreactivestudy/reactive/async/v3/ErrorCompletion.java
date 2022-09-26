package reactivestudy.springreactivestudy.reactive.async.v3;

import java.util.function.Consumer;

/**
 * Created by YC on 2022/09/26.
 */
public class ErrorCompletion<T> extends Completion<T, T> {

    Consumer<Throwable> consumer;

    public ErrorCompletion(Consumer<Throwable> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run(T value) {
        if (next != null) {
            next.run(value);
        }
    }

    @Override
    public void error(Throwable ex) {
        consumer.accept(ex);
    }
}
