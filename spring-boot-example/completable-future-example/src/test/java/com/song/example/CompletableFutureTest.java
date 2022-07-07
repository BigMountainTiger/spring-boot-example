package com.song.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// https://www.callicoder.com/java-8-completablefuture-tutorial/
// https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html#commonPool--
// https://mkyong.com/junit5/junit-5-maven-examples/

public class CompletableFutureTest {

    @Test
    public void The_trivial_example() throws InterruptedException, ExecutionException {

        final var result = "This is the result";
        var future = new CompletableFuture<String>();
        future.complete(result);

        Assertions.assertEquals(result, future.get());

    }

    @Test
    public void Running_asynchronous_computation_using_runAsync() throws InterruptedException, ExecutionException {
        System.out.println("Running_asynchronous_computation_using_runAsync()");

        var future = CompletableFuture.runAsync(() -> {

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                
                throw new IllegalStateException(e);
            }

            System.out.println("2 - future completed in a separate thread.");
            
        });

        System.out.println("1 - future created");
        future.get();
        System.out.println("3 - future get() completed");

        System.out.println();
    }


    
}
