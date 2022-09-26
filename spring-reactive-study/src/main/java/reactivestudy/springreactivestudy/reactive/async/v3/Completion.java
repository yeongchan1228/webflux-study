package reactivestudy.springreactivestudy.reactive.async.v3;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by YC on 2022/09/26.
 */
@SuppressWarnings("deprecation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Completion<S, T> {

    Completion next;

    public Completion<T, T> andError(Consumer<Throwable> consumer) {
        Completion<T, T> completion = new ErrorCompletion<>(consumer);
        this.next = completion;
        return completion;
    }

    public void andAccept(Consumer<T> consumer) {
        Completion<T, Void> completion = new AcceptCompletion<T>(consumer);
        this.next = completion;

    }

    public <V> Completion<T, V> andApply(Function<T, ListenableFuture<V>> function) {
        Completion<T, V> completion = new ApplyCompletion<>(function);
        this.next = completion;
        return completion;
    }

    public static <S, T> Completion<S, T> from(ListenableFuture<T> lf) {
        Completion<S, T> completion = new Completion<>();
        lf.addCallback(res -> {
            completion.complete(res);
        }, ex -> {
            completion.error(ex);
        });
        return completion;
    }

    public void run(S value) {

    }

    public void complete(T res) {
        if (next != null) {
            next.run(res);
        }
    }

    public void error(Throwable ex) {
        if (next != null) {
            next.error(ex);
        }
    }
}
