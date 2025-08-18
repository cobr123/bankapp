package com.example.bankapp.ui.model;

import lombok.Builder;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.time.LocalDate;

@Getter
@Builder
public class EditUserPasswordDto {
    private String password;
    private String confirmPassword;

    @ConstructorProperties({"password", "confirm_password"})
    public EditUserPasswordDto(String password, String confirmPassword) {
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}