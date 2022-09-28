package reactivestudy.springreactivestudy.reactive.async;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Created by YC on 2022/09/27.
 */
@Slf4j
public class CompletableFutureEx {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1
        /*
        CompletableFuture<Integer> f1 = CompletableFuture.completedFuture(1);
        System.out.println(f1.get());
        */

        // 2
        /*
        CompletableFuture<Integer> f2 = new CompletableFuture<>();
        f2.complete(2);
        System.out.println(f2.get());
        */

        // 3
        /*
        CompletableFuture<Integer> f3 =new CompletableFuture<>();
        f3.completeExceptionally(new RuntimeException()); // 예외를 담고 있는다.
        System.out.println(f3.get()); // get을 호출해야 예외가 발생한다.
        */

        // 4
        /*
        CompletableFuture.runAsync(() -> {
            log.info("runAsync");
        });
        log.info("exit");
        */

        // 5. 의존적 실행 : 동일 스레드 보장
        /*
        CompletableFuture
                .runAsync(() -> log.info("runAsync"))
                .thenRun(() -> log.info("thenRunAsync1"))
                .thenRun(() -> log.info("thenRunAsync2"));
        */

        // 6
        /*
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
                    return 1;
                })
                .thenApply(res -> {
                    log.info("theApply {}", res);
                    return res + 1;
                })
                .thenAccept(res -> log.info("thenAccept {}", res));
        */

        // 7
        /*
        CompletableFuture
                .supplyAsync(() -> {
                    return 1;
                })
                .thenCompose(res -> {
                    log.info("thenCompose {}", res);
                    return CompletableFuture.completedFuture( res + 1);
                })
                .thenAccept(res -> log.info("thenAccept {}", res));
        */

        // 8
        /*
        CompletableFuture
                .supplyAsync(() -> {
                    return 1;
                })
                .thenCompose(res -> {
                    log.info("thenCompose {}", res);
                    if (res == 1) throw new RuntimeException();
                    return CompletableFuture.completedFuture( res + 1);
                })
                .exceptionally(e -> {
                    return -10;
                })
                .thenAccept(res -> log.info("thenAccept {}", res));
        */

        // 9. 다른 스레드에서 비동기 작업 실행
        ExecutorService es = Executors.newFixedThreadPool(10);
        /*
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
                    return 1;
                }, es)
                .thenCompose(res -> {
                    log.info("thenCompose {}", res);
                    return CompletableFuture.completedFuture(res);
                })
                .thenApplyAsync(res -> {
                    log.info("thenApplyAsync {}", res);
                    return res + 10;
                }, es)
                .thenAcceptAsync(res -> log.info("thenAcceptAsync {}", res), es);
        */

        es.awaitTermination(3, TimeUnit.SECONDS);
        es.shutdown();
        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}
