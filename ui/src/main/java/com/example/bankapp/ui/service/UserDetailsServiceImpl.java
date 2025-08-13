package com.example.bankapp.ui.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import reactor.core.publisher.Mono;

import java.util.List;

public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserClient userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.findByName(username)
                .map(u -> (UserDetails) new User(u.getName(), u.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER"))))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(username)));
    }
}