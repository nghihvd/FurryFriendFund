package org.example.furryfriendfund.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByRoleIDOrderByCreate_atDesc(int roleID);
    List<Notification> findByAccountIDBOrderByCreated_atDesc(String accountID);

}
