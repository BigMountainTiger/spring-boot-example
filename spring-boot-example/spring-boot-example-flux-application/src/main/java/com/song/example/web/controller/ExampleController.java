package com.song.example.web.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.song.example.web.util.Auth;
import com.song.example.web.util.RG;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

@RestController
public class ExampleController {

    private DynamoDbAsyncClient client;
    private Auth auth;

    public ExampleController(Auth auth) {
        this.auth = auth;
        this.client = DynamoDbAsyncClient.builder().build();
    }

    @GetMapping("/test/auth")
    public Mono<ResponseEntity<Object>> auth(@RequestHeader Map<String, String> headers,
            @RequestParam Map<String, String> params) {

        Mono<ResponseEntity<Object>> mono = Mono.create(m -> {

            var authorization = headers.get("authorization");
            var auth = this.auth.auth(authorization);
            auth.whenCompleteAsync((r, e) -> {

                try {
                    if (e != null) {
                        m.success(RG.Response(HttpStatus.UNAUTHORIZED, e.getMessage()));
                        return;
                    }

                    var keyToGet = new HashMap<String, AttributeValue>();
                    keyToGet.put("id", AttributeValue.builder().s("1").build());
                    keyToGet.put("entry_time", AttributeValue.builder().s("entry_1").build());

                    var drq = GetItemRequest.builder().key(keyToGet).tableName("TABLE_1").build();
                    var f = this.client.getItem(drq);

                    f.whenCompleteAsync((dr, de) -> {

                        try {

                            if (de != null) {
                                throw de;
                            }

                            var item = dr.item();
                            var map = new HashMap<String, Object>();
                            map.put("id", item.get("id").s());
                            map.put("entry_time", item.get("entry_time").s());
                            map.put("attr", item.get("attr").s());

                            m.success(RG.Success(map));

                        } catch (Throwable ex) {

                            m.success(RG.Response(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
                        }

                    });

                } catch (Throwable ex) {

                    m.success(RG.Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
                }
            });

        });

        return mono;
    }

    @GetMapping("/test")
    public Mono<ResponseEntity<HashMap<String, Object>>> test() {

        Mono<ResponseEntity<HashMap<String, Object>>> mono = Mono.create(s -> {

            var keyToGet = new HashMap<String, AttributeValue>();
            keyToGet.put("id", AttributeValue.builder().s("1").build());
            keyToGet.put("entry_time", AttributeValue.builder().s("entry_1").build());

            var drq = GetItemRequest.builder().key(keyToGet).tableName("TABLE_1").build();
            var f = this.client.getItem(drq);

            f.thenAcceptAsync((res) -> {

                var item = res.item();

                var map = new HashMap<String, Object>();
                map.put("id", item.get("id").s());
                map.put("entry_time", item.get("entry_time").s());
                map.put("attr", item.get("attr").s());

                var response = ResponseEntity.status(HttpStatus.OK);
                s.success(response.body(map));

            }).exceptionally(e -> {

                var map = new HashMap<String, Object>(Map.of(
                        "Error", e.getMessage()));

                var response = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
                s.success(response.body(map));

                return null;
            });

        });

        mono = mono.timeout(Duration.ofSeconds(20));
        return mono;
    }

    @GetMapping("/test_delayed")
    public Mono<String> test_delayed() {

        Mono<String> m = Mono.create(s -> {

            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                s.success("OK");
            });

        });

        return m;
    }

}
