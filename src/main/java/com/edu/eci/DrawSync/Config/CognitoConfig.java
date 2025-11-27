package com.edu.eci.DrawSync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

/**
 * Spring configuration class that exposes AWS Cognito SDK clients as injectable beans.
 *
 * <p>This configuration provides a singleton {@code CognitoIdentityProviderClient} backed by the
 * AWS SDK v2. The client is immutable and thread-safe, making it suitable for reuse across the
 * application via Spring's dependency injection.</p>
 *
 * <p>Notes:
 * <ul>
 *   <li>The region is currently hard-coded to {@code us-east-2}. Consider externalizing it to
 *       application configuration (e.g., {@code application.yml}) for flexibility.</li>
 *   <li>Credentials are resolved via the AWS SDK default provider chain (env vars, system props,
 *       profile/credentials files, EC2/ECS/SSO providers, etc.). Override as needed.</li>
 *   <li>Lifecycle is managed by the Spring container; the client will be closed automatically
 *       on application shutdown.</li>
 * </ul>
 * </p>
 */

/**
 * Creates and configures a singleton {@code CognitoIdentityProviderClient} bean
 * using the AWS SDK v2.
 *
 * @return a thread-safe {@code CognitoIdentityProviderClient} configured for
 *         the {@code us-east-2} region
 * @implNote The region is fixed to {@code us-east-2}. Prefer externalizing to
 *           configuration if you need
 *           multi-environment or multi-region deployments.
 * @see software.amazon.awssdk.services.cognitoidp.CognitoIdentityProviderClient
 */
@Configuration
public class CognitoConfig {

    @Bean
    public CognitoIdentityProviderClient cognitoClient() {
        return CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_2)
                .build();
    }
}