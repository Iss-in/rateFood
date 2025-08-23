package com.ratefood.api_gateway.service.impl;

import com.ratefood.api_gateway.repository.UserRepository;
import com.ratefood.api_gateway.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.justOrEmpty(userRepository.findByEmail(username))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .cast(UserDetails.class);
    }
}
