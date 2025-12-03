package com.edu.eci.DrawSync.Config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    private final BearerTokenInterceptor tokenInterceptor;

    public RestConfig(BearerTokenInterceptor tokenInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .additionalInterceptors(tokenInterceptor)
                .build();
    }

}
