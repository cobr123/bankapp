package com.example.bankapp.accounts.controller;

import com.example.bankapp.accounts.configuration.SecurityConfig;
import com.example.bankapp.accounts.model.User;
import com.example.bankapp.accounts.model.UserLoginName;
import com.example.bankapp.accounts.model.UserMapper;
import com.example.bankapp.accounts.model.UserResponseDto;
import com.example.bankapp.accounts.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class})
@ActiveProfiles("test")
public class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TransferService transferService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private UserSettingsService userSettingsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;
    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService);
        Mockito.reset(transferService);
        Mockito.reset(accountService);
        Mockito.reset(userMapper);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser
    public void testGetAll() throws Exception {
        when(userService.findAllLoginName()).thenReturn(List.of(new UserLoginName("login2", "name2")));
        when(userMapper.toDto(any())).thenReturn(UserResponseDto.builder().build());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("login2"));
    }

    @Test
    @WithMockUser
    public void testGetAndFound() throws Exception {
        var user = User.builder().id(System.nanoTime()).build();
        when(userService.findByLogin(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toDto(any())).thenReturn(UserResponseDto.builder().build());

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
        when(userMapper.toDto(any())).thenReturn(UserResponseDto.builder().login(user.getLogin()).build());

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
        when(userSettingsService.changePassword(any(), anyString())).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(UserResponseDto.builder().login(user.getLogin()).build());

        String requestBody = "{\"password\": \"123\"}";

        mockMvc.perform(post("/john/editPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(user.getLogin()));
    }

    @Test
    @WithMockUser
    public void testEditUserAccounts() throws Exception {
        var user = User.builder().login("john").build();
        when(userService.findByLogin(anyString())).thenReturn(Optional.of(user));
        when(userSettingsService.editUser(any(), any())).thenReturn(user);
        when(transferService.updateUserCurrency(any(), any())).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(UserResponseDto.builder().login(user.getLogin()).build());

        String requestBody = "{\"name\": \"new name\", \"email\": \"new email\", \"birthdate\": \"" + LocalDate.now().minusYears(19) + "\"}";

        mockMvc.perform(post("/john/editUserAccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(user.getLogin()));
    }

    @Test
    @WithMockUser
    public void testTransfer() throws Exception {
        String requestBody = "[" +
                "{\"currency\": \"RUB\", \"before\": \"1\", \"after\": \"2\", \"login\": \"mary\"}," +
                "{\"currency\": \"RUB\", \"before\": \"2\", \"after\": \"1\", \"login\": \"john\"}" +
                "]";

        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk());
    }

}
