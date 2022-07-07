package com.song.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;


public class CompletableFuture_chain_Test {

    public int create_error() {
        int i = 1;
        int j = 0;

        return i/j;
    }

    @Test
    public void ATest() throws InterruptedException, ExecutionException {
        var future = new CompletableFuture<Integer>();
        
        CompletableFuture.supplyAsync(() -> {
            return 1;
        }).thenApply((x) -> {

            var f = new CompletableFuture<Integer>();
            CompletableFuture.runAsync(() -> {
                
                f.complete(x + 1);
            });

            return f;

        }).thenAcceptAsync((x) -> {

            System.out.println(x);

            try {
                future.complete(x.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }).exceptionally(e -> {
            System.out.println("Exception caught ------------------------------------------------ ");
            return null;
        });;
        
        future.thenAcceptAsync((x) -> {
            System.out.println("future resolved " + x);
        });

        System.out.println("Not blocked to here");

        java.util.concurrent.ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);
        System.out.println();
    }
    
}
