package reactive.async;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by YC on 2022/09/22.
 */
public class FutureEx {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        // 1. 비동기 확인
        es.execute(() -> {
            try {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + " Async");
            } catch (InterruptedException e) {}
        });
        System.out.println(Thread.currentThread().getName() + " Hello!");

        // 2. 비동기 실행 결과 받기 Future -> get()
        Future<String> submit = es.submit(() -> {
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(2000);
            return Thread.currentThread().getName() + " Async";
        });

        // main thread로 다른 작업 가능

        System.out.println("submit = " + submit.get()); // main thread Blocking

        // 3. Future -> isDone() : 작업이 완료 되었는지 확인, 완료 시 - true, 미완료 시 - false
        Future<String> submit = es.submit(() -> {
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(2000);
            return Thread.currentThread().getName() + " Async";
        });

        System.out.println("submit = " + submit.isDone()); // main thread Blocking

        // 4. callBack
        FutureTask<String> futureTask = new FutureTask<>(
            () -> {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(2000);
                return Thread.currentThread().getName() + " Async";
        }) {
            @Override
            protected void done() {
                try {
                    System.out.println("futureTask = " + get());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        es.execute(futureTask);


        // 5. myCallback
        CallbackFutureTask callbackFutureTask = new CallbackFutureTask(
            () -> {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(2000);
                return Thread.currentThread().getName() + " Async";
            },
            System.out::println, // 성공 처리 : Callback
            System.out::println // 에러 처리 : Callback
        );

        es.execute(callbackFutureTask);

        System.out.println(Thread.currentThread().getName() + " Exit");
        es.shutdown();
    }

    interface SuccessCallback {
        void onSuccess(String result);
    }

    interface ExceptionCallback {
        void onError(Throwable throwable);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback successCallback;
        ExceptionCallback exceptionCallback;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback successCallback,
                                  ExceptionCallback exceptionCallback) {
            super(callable);
            this.successCallback = Objects.requireNonNull(successCallback);
            this.exceptionCallback = Objects.requireNonNull(exceptionCallback);
        }

        @Override
        protected void done() {
            try {
                successCallback.onSuccess(get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                exceptionCallback.onError(e.getCause());
            }
        }
    }
}
