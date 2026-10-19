package com.shopsmart.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret-key}")
    private String secret;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =====================================================
                // CORS
                // =====================================================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // =====================================================
                // CSRF
                // =====================================================

                .csrf(csrf -> csrf.disable())

                // =====================================================
                // STATELESS SESSION
                // =====================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =====================================================
                // AUTHORIZATION
                // =====================================================

                .authorizeHttpRequests(auth -> auth

                        // Browser preflight
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // =================================================
                        // GET PRODUCTS
                        // Anyone can view products
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/shopsmart/product/**"
                        ).permitAll()

                        // =================================================
                        // CUSTOMER STOCK USAGE
                        // =================================================

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/shopsmart/product/*/use-stock"
                        ).hasRole("customer")

                        // =================================================
                        // RETAILER CREATE PRODUCT
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/shopsmart/product/**"
                        ).hasRole("RETAILER")

                        // =================================================
                        // RETAILER UPDATE PRODUCT
                        // =================================================

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/shopsmart/product/**"
                        ).hasRole("retailer")

                        // =================================================
                        // RETAILER DELETE PRODUCT
                        // =================================================

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/shopsmart/product/**"
                        ).hasRole("retailer")

                        // =================================================
                        // RETAILER ADD STOCK
                        // =================================================

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/shopsmart/product/**"
                        ).hasRole("retailer")

                        // =================================================
                        // EVERYTHING ELSE
                        // =================================================

                        .anyRequest().authenticated()
                )

                // =====================================================
                // JWT RESOURCE SERVER
                // =====================================================

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                );

        return http.build();
    }


    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // =========================================================
    // JWT DECODER
    // =========================================================

    @Bean
    public JwtDecoder jwtDecoder() {

        SecretKey key =
                new SecretKeySpec(
                        secret.getBytes(),
                        "HmacSHA256"
                );

        return NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }


    // =========================================================
    // JWT AUTHORITY CONVERTER
    // =========================================================

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter
                grantedAuthoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        // JWT contains lowercase roles:
        //
        // "role": "customer"
        // "role": "retailer"

        grantedAuthoritiesConverter
                .setAuthoritiesClaimName("role");

        // customer -> ROLE_customer
        // retailer -> ROLE_retailer

        grantedAuthoritiesConverter
                .setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter
                jwtAuthenticationConverter =
                new JwtAuthenticationConverter();

        jwtAuthenticationConverter
                .setJwtGrantedAuthoritiesConverter(
                        grantedAuthoritiesConverter
                );

        return jwtAuthenticationConverter;
    }
}