package com.example.bankapp.accounts.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    
    private String login;
    private String password;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private BigDecimal balance;
}