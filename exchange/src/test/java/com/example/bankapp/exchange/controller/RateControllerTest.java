package com.example.bankapp.exchange.controller;

import com.example.bankapp.exchange.configuration.SecurityConfig;
import com.example.bankapp.exchange.model.Currency;
import com.example.bankapp.exchange.model.Rate;
import com.example.bankapp.exchange.service.RateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RateController.class)
@Import({SecurityConfig.class})
@ActiveProfiles("test")
public class RateControllerTest {

    @MockitoBean
    private RateService rateService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        Mockito.reset(rateService);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetAll() throws Exception {
        when(rateService.findAll()).thenReturn(List.of(Rate.builder().currency(Currency.RUB).build()));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("RUB"));
    }

    @Test
    @WithMockUser
    public void testPostWithAuth() throws Exception {
        String requestBody = "[{\"currency\": \"RUB\", \"value\": \"1\"}]";
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isCreated());
    }

    @Test
    public void testPostNoAuth() throws Exception {
        String requestBody = "[{\"currency\": \"RUB\", \"value\": \"1\"}]";

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isUnauthorized());
    }

}
