package com.example.bankapp.transfer.repository;

import com.example.bankapp.transfer.model.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationsRepository extends JpaRepository<EmailNotification, Long> {
}