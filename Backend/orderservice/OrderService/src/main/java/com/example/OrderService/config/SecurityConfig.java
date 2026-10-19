package com.example.OrderService.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret-key}")
    private String secret;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // ==============================
                // CSRF
                // ==============================

                .csrf(csrf -> csrf.disable())


                // ==============================
                // CORS
                // ==============================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )


                // ==============================
                // SESSION
                // ==============================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // ==============================
                // AUTHORIZATION
                // ==============================

                .authorizeHttpRequests(auth -> auth

                        // ==============================
                        // SWAGGER
                        // ==============================

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()


                        // ==============================
                        // INTERNAL AI SERVICE ENDPOINT
                        // ==============================

                        .requestMatchers(
                                "/shopsmart/orders/history/all-dump"
                        ).permitAll()


                        // ==============================
                        // CUSTOMER ORDER APIs
                        // ==============================
                        // JWT contains:
                        // "role": "customer"
                        //
                        // Converter creates:
                        // ROLE_customer
                        //
                        // Therefore use:
                        // hasRole("customer")

                        .requestMatchers(
                                "/shopsmart/orders/customer/**"
                        ).hasRole("customer")


                        // ==============================
                        // RETAILER ORDER APIs
                        // ==============================
                        // JWT contains:
                        // "role": "retailer"
                        //
                        // Converter creates:
                        // ROLE_retailer
                        //
                        // Therefore use:
                        // hasRole("retailer")

                        .requestMatchers(
                                "/shopsmart/orders/shop/**"
                        ).hasRole("RETAILER")


                        // ==============================
                        // EVERYTHING ELSE
                        // ==============================

                        .anyRequest().authenticated()
                )


                // ==============================
                // JWT RESOURCE SERVER
                // ==============================

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                );

        return http.build();
    }


    // ==============================
    // CORS CONFIGURATION
    // ==============================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
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


    // ==============================
    // JWT DECODER
    // ==============================

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


    // ==============================
    // JWT AUTHENTICATION CONVERTER
    // ==============================

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter
                grantedAuthoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        /*
         * Read role from JWT:
         *
         * "role": "customer"
         *
         * or
         *
         * "role": "retailer"
         */

        grantedAuthoritiesConverter
                .setAuthoritiesClaimName("role");

        grantedAuthoritiesConverter
                .setAuthorityPrefix("ROLE_");


        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                grantedAuthoritiesConverter
        );

        return converter;
    }
}