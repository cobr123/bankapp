package com.example.bankapp.accounts.controller;

import com.example.bankapp.accounts.AccountsApplicationTests;
import com.example.bankapp.accounts.configuration.SecurityConfig;
import com.example.bankapp.accounts.model.User;
import com.example.bankapp.accounts.model.UserMapper;
import com.example.bankapp.accounts.model.UserResponseDto;
import com.example.bankapp.accounts.service.UserService;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, AccountsApplicationTests.class})
@ActiveProfiles("test")
public class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser
    public void testGetAndFound() throws Exception {
        var user = User.builder().id(System.nanoTime()).build();
        when(userService.findByLogin(anyString())).thenReturn(Optional.of(user));
        when(mapper.toDto(any())).thenReturn(UserResponseDto.builder().build());

        mockMvc.perform(get("/userLogin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(user.getLogin()));
    }

    @Test
    @WithMockUser
    public void testGetAndNotFound() throws Exception {
        when(userService.findByLogin(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/userLogin"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testPostForm() throws Exception {
        when(userService.findByLogin(anyString())).thenReturn(Optional.empty());

        var user = User.builder().login("userLogin").build();
        when(userService.createUser(any())).thenReturn(user);
        when(mapper.toDto(any())).thenReturn(UserResponseDto.builder().login(user.getLogin()).build());

        String requestBody = "{\"login\": \"john\", \"password\": \"123\", \"confirm_password\": \"123\", \"name\": \"Doe John\", \"birthdate\": \"" + LocalDate.now().minusYears(19) + "\"}";

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value(user.getLogin()));
    }

    @Test
    @WithMockUser
    public void testPostFormFailed() throws Exception {
        when(userService.createUser(any())).thenThrow(new IllegalArgumentException("test"));

        String requestBody = "{\"login\": \"john\", \"password\": \"123\", \"confirm_password\": \"123\", \"name\": \"Doe John\", \"birthdate\": \"" + LocalDate.now().minusYears(19) + "\"}";

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("test"));
    }

    @Test
    @WithMockUser
    public void testEditPassword() throws Exception {
        var user = User.builder().login("john").build();
        when(userService.findByLogin(anyString())).thenReturn(Optional.of(user));
        when(userService.update(any())).thenReturn(user);
        when(mapper.toDto(any())).thenReturn(UserResponseDto.builder().login(user.getLogin()).build());

        String requestBody = "{\"password\": \"123\"}";

        mockMvc.perform(post("/john/editPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(user.getLogin()));
    }

}
