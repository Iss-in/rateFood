package com.ratefood.api_gateway.config;

import com.ratefood.api_gateway.service.JwtService;
import com.ratefood.api_gateway.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final PathPattern foodAppPattern = new PathPatternParser().parse("/**");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String jwt = authHeader.substring(7);
        String userEmail;

        try {
            userEmail = jwtService.extractUserName(jwt);
            log.debug("Extracted userEmail from JWT: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to extract username from JWT: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        if (userEmail != null) {
            return userService.findByUsername(userEmail)
                    .filter(userDetails -> {
                        boolean isValid = jwtService.isTokenValid(jwt, userDetails);
                        log.debug("Token validation for user {}: {}", userDetails.getUsername(), isValid);
                        return isValid;
                    })
                    .flatMap(userDetails -> {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        // Format roles properly for downstream services
                        String roles = userDetails.getAuthorities().stream()
                                .map(auth -> {
                                    String authority = auth.getAuthority();
                                    // Ensure role starts with ROLE_ prefix
                                    if (!authority.startsWith("ROLE_")) {
                                        authority = "ROLE_" + authority;
                                    }
                                    return authority;
                                })
                                .reduce((a, b) -> a + "," + b)
                                .orElse("");

                        log.debug("Setting headers for user: {}, roles: {}", userDetails.getUsername(), roles);

                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(r -> r.header("X-User-Email", userDetails.getUsername())
                                        .header("X-User-Id", String.valueOf(((com.ratefood.api_gateway.entity.User) userDetails).getId()))
                                        .header("X-User-Roles", roles))
                                .build();

                        return chain.filter(modifiedExchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                    })
                    .switchIfEmpty(Mono.defer(() -> {
//                        log.warn("Authentication failed for user: {}", userEmail);
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }));
        }

        return chain.filter(exchange);
    }
}