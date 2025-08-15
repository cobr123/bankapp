package com.example.bankapp.ui.model;

import lombok.Builder;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.time.LocalDate;

@Getter
@Builder
public class RegisterUserRequestDto {
    private String login;
    private String password;
    private String confirmPassword;
    private String name;
    private LocalDate birthdate;

    @ConstructorProperties({"login", "password", "confirm_password", "name", "birthdate"})
    public RegisterUserRequestDto(String login, String password, String confirmPassword, String name, LocalDate birthdate) {
        this.login = login;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
        this.birthdate = birthdate;
    }
}