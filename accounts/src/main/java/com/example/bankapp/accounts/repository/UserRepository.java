package com.example.bankapp.accounts.repository;

import com.example.bankapp.accounts.model.User;
import com.example.bankapp.accounts.model.UserLoginName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    @Query(value = "SELECT login, name FROM users", nativeQuery = true)
    List<UserLoginName> findAllLoginName();
}