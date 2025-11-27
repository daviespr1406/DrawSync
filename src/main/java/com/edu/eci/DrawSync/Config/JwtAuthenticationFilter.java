package com.edu.eci.DrawSync.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter that intercepts HTTP requests and validates JWT
 * tokens.
 * Extracts the token from the Authorization header and validates it using
 * CognitoJwtValidator.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CognitoJwtValidator jwtValidator;

    public JwtAuthenticationFilter(CognitoJwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        System.out.println("JwtAuthenticationFilter: Processing request to " + request.getRequestURI());

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                // Validate token
                Claims claims = jwtValidator.validateToken(token);
                String username = jwtValidator.getUsernameFromClaims(claims);

                // Create authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("JWT Authentication successful for user: " + username);

            } catch (JwtException e) {
                System.err.println("JWT validation failed: " + e.getMessage());
                // Don't set authentication - request will be rejected by Spring Security if
                // endpoint requires auth
            }
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
