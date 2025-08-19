package com.ratefood.api_gateway.config;

import com.ratefood.api_gateway.service.JwtService;
import com.ratefood.api_gateway.service.UserService;
import lombok.RequiredArgsConstructor;
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

//        if (exchange.getRequest().getMethod() == HttpMethod.GET &&
//                foodAppPattern.matches(exchange.getRequest().getPath().pathWithinApplication())
//        ) {
//            return chain.filter(exchange);
//        }




        String jwt = authHeader.substring(7);
        String userEmail;
        try {
            userEmail = jwtService.extractUserName(jwt);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        if (userEmail != null) {
            return userService.findByUsername(userEmail)
                    .filter(userDetails -> jwtService.isTokenValid(jwt, userDetails))
                    .flatMap(userDetails -> {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(r -> r.header("X-User-Email", userDetails.getUsername())
                                        .header("X-User-Id", String.valueOf(((com.ratefood.api_gateway.entity.User) userDetails).getId()))
                                        .header("X-User-Roles", userDetails.getAuthorities().stream()
                                                .map(Object::toString)
                                                .reduce((a, b) -> a + "," + b)
                                                .orElse("")))
                                .build();

                        return chain.filter(modifiedExchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }));
        }

        return chain.filter(exchange);
    }
}
