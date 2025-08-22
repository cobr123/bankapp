package com.example.bankapp.cash.repository;

import com.example.bankapp.cash.model.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationsRepository extends JpaRepository<EmailNotification, Long> {
}