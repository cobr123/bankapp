package com.example.bankapp.ui.service;

import com.example.bankapp.ui.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserClient userClient;

    @Override
    public Mono<UserDetails> findByUsername(String login) {
        return userClient.findByLogin(login)
                .map(u -> (UserDetails) new User(u.getLogin(), u.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER"))))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(login)));
    }
}