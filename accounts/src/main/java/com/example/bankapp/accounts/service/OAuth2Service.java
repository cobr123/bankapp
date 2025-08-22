package com.example.bankapp.accounts.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final OAuth2AuthorizedClientManager manager;

    public String getTokenValue() {
        return manager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId("keycloak-rest-client")
                        .principal("system")
                        .build()
                )
                .getAccessToken()
                .getTokenValue();
    }

}
