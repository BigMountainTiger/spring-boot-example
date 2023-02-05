package com.song.example;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import com.song.util.Authorizer;


public class CompletableFuture_auth_Test {

    @Test
    public void whenCompleteAsync() throws InterruptedException, ExecutionException {

        var auth = Authorizer.authorize();


        var result = auth.get();
        System.out.println(result);

    }

}
