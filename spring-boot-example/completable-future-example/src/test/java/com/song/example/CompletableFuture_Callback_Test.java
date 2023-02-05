package com.song.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class CompletableFuture_Callback_Test {

    @Test
    public void Running_asynchronous_computation_using_runAsync() throws InterruptedException, ExecutionException {
        System.out.println("Running_asynchronous_computation_using_runAsync()");

        var future = CompletableFuture.runAsync(() -> {

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

                throw new IllegalStateException(e);
            }

            System.out.println("3 - future completed in a separate thread.");
            // throw new IllegalStateException("OK - a exception");

        });

        System.out.println("1 - future created");
        future.thenAcceptAsync((x) -> {

            System.out.println("4 - future triggered thenAcceptAsync()");
        }).exceptionally(e -> {

            System.out.println("Exception captured" + e.getMessage());
            // e.printStackTrace();
            return null;
        });

        System.out.println("2 - future get() completed");

        java.util.concurrent.ForkJoinPool.commonPool().awaitQuiescence(3, TimeUnit.SECONDS);
        System.out.println();
    }

}
