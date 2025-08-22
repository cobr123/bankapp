package com.example.bankapp.accounts.repository;

import com.example.bankapp.accounts.model.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationsRepository extends JpaRepository<EmailNotification, Long> {
}