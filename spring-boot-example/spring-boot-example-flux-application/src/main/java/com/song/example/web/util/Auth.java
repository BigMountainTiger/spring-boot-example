package com.song.example.web.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import software.amazon.awssdk.utils.StringUtils;

// https://www.baeldung.com/java-jwt-token-decode
// https://medium.com/trabe/validate-jwt-tokens-using-jwks-in-java-214f7014b5cf
public class Auth {

    private final HashMap<String, Object> config;

    public Auth(HashMap<String, Object> config) {
        this.config = config;
    }

    public void init() throws IOException, InterruptedException, InvalidPublicKeyException {
        String URL = config.get("url").toString();

        var client = HttpClient.newBuilder().build();
        var request = HttpRequest.newBuilder(URI.create(URL)).GET().build();

        var response = client.send(request, BodyHandlers.ofString());
        var content = new JSONObject(response.body());
        var keys = content.getJSONArray("keys");

        var keyMap = new HashMap<String, Object>();
        for (var i = 0; i < keys.length(); i++) {
            var key = keys.getJSONObject(i);
            var kid = key.getString("kid");
            var publicKey = Jwk.fromValues(key.toMap()).getPublicKey();

            keyMap.put(kid, publicKey);
        }

        config.put("keys", keyMap);
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture<Boolean> auth(String authorization) {
        var authFuture = new CompletableFuture<Boolean>();

        CompletableFuture.runAsync(() -> {

            try {

                if (StringUtils.isEmpty(authorization)) {
                    throw new Exception("No authorization header received");
                }

                var token = authorization.split(" ")[1];
                var jwt = JWT.decode(token);

                var kid = jwt.getKeyId();
                var issuer = jwt.getIssuer();
                var scope = jwt.getClaims().get("scope").asArray(String.class);

                if (!issuer.equals(config.get("issuer"))) {
                    throw new Exception("Could not validate the auth issuer");
                }

                if (!Arrays.asList(scope).contains(config.get("scope"))) {
                    throw new Exception("Could not validate the auth scope");
                }

                var publicKey = (RSAPublicKey) ((HashMap<String, Object>) config.get("keys")).get(kid);
                Algorithm algorithm = Algorithm.RSA256(publicKey, null);

                algorithm.verify(jwt);

                var expire = jwt.getExpiresAt();
                var now = Calendar.getInstance().getTime();
                if (expire.before(now)) {
                    throw new RuntimeException("Token expired");
                }

                authFuture.complete(true);

            } catch (Throwable e) {
                authFuture.completeExceptionally(e);
            }
        });

        return authFuture;

    }
}
