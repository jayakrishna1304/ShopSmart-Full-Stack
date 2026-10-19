package com.example.VoucherService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;


    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()
                ))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Health check
                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()

                        // -------------------------------------------------
                        // RETAILER OPERATIONS
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/voucher"
                        )
                        .hasRole("RETAILER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/voucher/**"
                        )
                        .hasRole("RETAILER")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/voucher/**"
                        )
                        .hasRole("RETAILER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/voucher/**"
                        )
                        .hasRole("RETAILER")

                        // -------------------------------------------------
                        // READ OPERATIONS
                        // Customer + Retailer
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/voucher/**"
                        )
                        .authenticated()

                        // Everything else
                        .anyRequest()
                        .authenticated()
                )

                // ---------------------------------------------------------
                // JWT RESOURCE SERVER
                // ---------------------------------------------------------

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
    // JWT DECODER
    //
    // Uses the SAME secret used by Auth Service.
    // =========================================================

    @Bean
    public JwtDecoder jwtDecoder() {

        SecretKeySpec secretKeySpec =
                new SecretKeySpec(
                        jwtSecretKey.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        "HmacSHA256"
                );

        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .build();
    }


    // =========================================================
    // JWT ROLE CONVERTER
    //
    // JWT:
    //
    // "role": "RETAILER"
    //
    // becomes:
    //
    // ROLE_RETAILER
    // =========================================================

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter
                grantedAuthoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        grantedAuthoritiesConverter
                .setAuthoritiesClaimName("role");

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


    // =========================================================
    // CORS
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
                        "PATCH",
                        "DELETE",
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

        configuration.setExposedHeaders(
                List.of(
                        "Authorization"
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
}