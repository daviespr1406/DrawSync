package com.edu.eci.DrawSync.config;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * Intercepts an outgoing HTTP request, adding an {@code Authorization: Bearer}
 * header when a token is available.
 *
 * <p>
 * If the {@code TokenProvider} returns {@code null} or a blank token, the
 * header is not added and the request
 * proceeds unchanged. Otherwise, the method sets the Bearer token and delegates
 * execution.
 * </p>
 *
 * <p>
 * This method delegates exactly once to
 * {@code execution.execute(request, body)}.
 * </p>
 *
 * @param request   the HTTP request to execute
 * @param body      the request body as a byte array
 * @param execution the request execution callback
 * @return the HTTP response
 * @throws java.io.IOException if an I/O error occurs during request execution
 */
@Component
public class BearerTokenInterceptor implements ClientHttpRequestInterceptor {

    private final TokenProvider tokenProvider;

    public BearerTokenInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        String token = tokenProvider.getAccessToken();
        if (token != null && !token.isBlank()) {
            request.getHeaders().setBearerAuth(token);
        }

        return execution.execute(request, body);
    }

}
