package org.example.furryfriendfund.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByRoleIDOrderByCreatedAtDesc(int roleID);
    List<Notification> findByAccountIDOrderByCreatedAtDesc(String accountID);
    void deleteByCreatedAtBefore(LocalDateTime createdAt);
}
