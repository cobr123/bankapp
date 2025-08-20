package com.example.bankapp.accounts.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
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
    private LocalDate birthdate;
}