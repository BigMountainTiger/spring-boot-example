package com.song.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.asynchttpclient.Dsl;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

// https://www.viralpatel.net/java-create-validate-jwt-token/

public class Authorizer {

    public static final String AUTH_URL;
    public static final String ISSUER;
    public static final String SCOPE;

    static {
        Dotenv env = Dotenv.load();
        AUTH_URL = env.get("AUTH_URL");
        ISSUER = env.get("ISSUER");
        SCOPE = env.get("SCOPE");
    }

    private static CompletableFuture<String> auth_key() {
        var future = new CompletableFuture<String>();

        CompletableFuture.runAsync(() -> {
            var client = Dsl.asyncHttpClient();
            var request = Dsl.get(AUTH_URL).build();
            var key_future = client.executeRequest(request);

            key_future.addListener(() -> {
                try {
                    var response = key_future.get();
                    var body = response.getResponseBody();
                    var keys = new JSONObject(body).getJSONArray("keys");
                    
                    for (int i = 0; i < keys.length(); i++) {
                        // JSONObject key = keys.getJSONObject(i);
                        System.out.println(i);
                    }

                    future.complete("OK");

                } catch (InterruptedException | ExecutionException e) {

                    e.printStackTrace();
                    future.completeExceptionally(e);
                }

            }, Executors.newCachedThreadPool());

        });

        return future;
    }

    public static CompletableFuture<Boolean> authorize() {
        var future = new CompletableFuture<Boolean>();

        var key = auth_key();
        key.whenComplete((r, e) -> {

            future.complete(true);

        });

        return future;
    }
}
