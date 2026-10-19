package com.example.AIServicenew.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        String url = template.url();

        /*
         * IMPORTANT:
         * These are internal AI-service endpoints.
         *
         * We DO NOT forward the user's JWT to these endpoints.
         * They are configured as permitAll() in Cart and Order services.
         */
        if (url.contains("/shopsmart/cart/items/all-dump")
                || url.contains("/shopsmart/orders/history/all-dump")) {

            System.out.println(
                    "AI SERVICE - Internal endpoint detected. JWT NOT forwarded: "
                            + url
            );

            return;
        }

        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            System.out.println(
                    "AI SERVICE - No current HTTP request context"
            );
            return;
        }

        HttpServletRequest request =
                attributes.getRequest();

        String authorizationHeader =
                request.getHeader("Authorization");

        System.out.println(
                "AI SERVICE - Incoming Authorization: "
                        + (authorizationHeader != null
                        ? "PRESENT"
                        : "MISSING")
        );

        if (authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")) {

            template.header(
                    "Authorization",
                    authorizationHeader
            );

            System.out.println(
                    "AI SERVICE - JWT forwarded to: "
                            + template.url()
            );
        }
    }
}