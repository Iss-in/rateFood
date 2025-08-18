package com.ratefood.api_gateway.service;

import com.ratefood.api_gateway.dto.JwtAuthenticationResponse;
import com.ratefood.api_gateway.dto.SignInRequest;
import com.ratefood.api_gateway.dto.SignUpRequest;

import reactor.core.publisher.Mono;

public interface AuthenticationService {
    Mono<JwtAuthenticationResponse> signup(SignUpRequest request);

    Mono<JwtAuthenticationResponse> signin(SignInRequest request);
}
