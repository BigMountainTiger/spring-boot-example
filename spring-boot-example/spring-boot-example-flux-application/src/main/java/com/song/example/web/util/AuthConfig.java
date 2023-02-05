package com.song.example.web.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.auth0.jwk.InvalidPublicKeyException;

@Configuration
public class AuthConfig {
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Auth getAuth() throws IOException, InterruptedException, InvalidPublicKeyException {
        Map<String, String> env = System.getenv();
        
        var config = new HashMap<String, Object>();
        config.put("url", env.get("URL"));
        config.put("issuer", env.get("ISSUER"));
        config.put("scope", env.get("SCOPE"));

        var auth = new Auth(config);

        auth.init();

        return auth;
    }
}
