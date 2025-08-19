package com.ratefood.app.configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig {
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("*")   // allow all origins
//                        .allowedOrigins("http://localhost:3000")
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedHeaders("*");
//            }
//        };
//    }
//}

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

@Slf4j
@Configuration
public class CorsConfig {

//    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Bean
    public OncePerRequestFilter logRequestHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {

                log.info("----- Incoming request: {} {}", request.getMethod(), request.getRequestURI());

                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    Collections.list(headerNames).forEach(headerName -> {
                        Enumeration<String> headers = request.getHeaders(headerName);
                        Collections.list(headers).forEach(headerValue -> {
                            log.info("Header: {} = {}", headerName, headerValue);
                        });
                    });
                }

                filterChain.doFilter(request, response);
            }
        };
    }
}
