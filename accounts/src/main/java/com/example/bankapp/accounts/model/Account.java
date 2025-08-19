package com.example.bankapp.accounts.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Currency currency;
    private BigDecimal value;
}
