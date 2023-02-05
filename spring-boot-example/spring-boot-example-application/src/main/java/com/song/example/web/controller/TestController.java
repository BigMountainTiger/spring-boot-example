package com.song.example.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.song.example.data.TestData;
import com.song.example.viewmodel.TestVM;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    TestData testData;

    @GetMapping
    public List<String> aGetMethod(HttpServletRequest request) {

        var hnames = request.getHeaderNames();
        while (hnames.hasMoreElements()) {
            var header = hnames.nextElement();
            System.out.println(header + " - " + request.getHeader(header));
        }

        var s = this.testData.getS();
        return s;
    }

    // https://blog.davidvassallo.me/2019/11/04/speeding-up-spring-mvc-with-completablefuture/
    @GetMapping("/future-0")
    public CompletableFuture<String> aFutureMethod_0(HttpServletRequest request) {
        var future = new CompletableFuture<String>();
        future.orTimeout(5, TimeUnit.SECONDS);

        CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS).execute(() -> {

            System.out.println("Completing in 2 second");
            future.complete("OK - 2");
        });

        CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> {

            System.out.println("Completing in 3 second");
            future.complete("OK - 3");
        });

        System.out.println("Returning the future");

        return future;
    }

    @GetMapping("/future-1")
    public CompletableFuture<String> aFutureMethod_1(HttpServletRequest request) {
        var future = new CompletableFuture<String>();

        CompletableFuture.runAsync(() -> {

            var client = DynamoDbAsyncClient.builder().build();

            var keyToGet = new HashMap<String, AttributeValue>();
            keyToGet.put("id", AttributeValue.builder().s("1").build());
            keyToGet.put("entry_time", AttributeValue.builder().s("entry_1").build());

            var drq = GetItemRequest.builder().key(keyToGet).tableName("TABLE_1").build();

            var f = client.getItem(drq);

            f.thenAcceptAsync((res) -> {

                System.out.println("Dynamoclient call completed");

                var item = res.item();
                var entry = item.get("id").s();

                System.out.println("id = " + entry);

                var values = item.values();
                var map = values.stream().collect(Collectors.toMap(AttributeValue::s, s -> s));
                var keys = map.keySet();
                for (var sinKey : keys) {
                    System.out.format("%s: %s\n", sinKey, map.get(sinKey).toString());
                }

                future.complete(entry);

            }).exceptionally(e -> {
                System.out.println("Exception caught ------------------------------------------------ ");
                // future.completeExceptionally(new Exception("Wrong"));
                future.complete("Something wrong");
                return null;
            });

            System.out.println("Dynamoclient call scheduled");

        });

        System.out.println("future is returned to spring");
        return future;
    }

    @PostMapping("/post")
    public TestVM postController(
            @RequestBody TestVM vm,
            HttpServletRequest request) {

        var hnames = request.getHeaderNames();
        while (hnames.hasMoreElements()) {
            var header = hnames.nextElement();
            System.out.println(header + " - " + request.getHeader(header));
        }

        return vm;
    }

}
