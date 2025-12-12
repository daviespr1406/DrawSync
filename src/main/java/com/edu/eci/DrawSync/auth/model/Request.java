package com.edu.eci.DrawSync.auth.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * The {@code Request} class is a Spring component that encapsulates
 * configuration properties
 * and a {@link RestTemplate} instance for making HTTP requests. It retrieves
 * the base URL,
 * client ID, and client secret from application properties using the
 * {@code @Value} annotation.
 * <p>
 * This class provides getter methods for accessing these configuration values
 * and the
 * {@link RestTemplate} instance.
 * </p>
 *
 * <p>
 * Example usage:
 * 
 * <pre>
 * @Autowired
 * private Request request;
 * 
 * String baseUrl = request.getBaseUrl();
 * RestTemplate restTemplate = request.getRestTemplate();
 * </pre>
 * </p>
 *
 * @author DrawSync development Team
 */
@Component
public class Request {

    @Value("${base.url}")
    private String baseUrl;

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @Value("${client.redirect-uri}")
    private String redirectUri;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
