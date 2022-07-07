package com.song.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

// 1. orTimeout complete with exception when timeout
// 2. A future can be completed / completeExceptionally multiple time, any further actions are ignored
public class CompletableFuture_whencomplete_Test {

    @Test
    public void whenCompleteAsync() throws InterruptedException, ExecutionException {

        final var result = "This is the result";
        CompletableFuture<String> future = new CompletableFuture<String>()
                .orTimeout(1, TimeUnit.SECONDS);

        future.whenCompleteAsync((r, e) -> {
            System.out.println(r);
            System.out.println(e);
        });

        CompletableFuture.runAsync(() -> {
            try {

                TimeUnit.SECONDS.sleep(8);
            } catch (InterruptedException e) {

                throw new IllegalStateException(e);
            }

            future.complete(result);
            future.completeExceptionally(new Exception("Exception"));

        });

        java.util.concurrent.ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);
    }

}
