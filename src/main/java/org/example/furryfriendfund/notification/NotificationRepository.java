package org.example.furryfriendfund.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByRoleIDOrderByCreatedAtDesc(int roleID);
    List<Notification> findByAccountIDOrderByCreatedAtDesc(String accountID);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM notifications WHERE created_at < :createdAt AND accountID IS NULL AND petID IS NULL", nativeQuery = true)
    int deleteByCreatedAtBefore(@Param("createdAt") Timestamp createdAt);



}
