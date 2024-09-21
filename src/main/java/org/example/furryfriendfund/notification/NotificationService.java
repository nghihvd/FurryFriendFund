package org.example.furryfriendfund.notification;

import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.accounts.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class NotificationService implements INotificationService{

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private AccountsRepository accountsRepository;

    @Override
    public Notification createRegisterNotification(Accounts accounts) {
            Notification notification = new Notification();
            notification.setNotiID(UUID.randomUUID().toString().substring(0, 8));
            notification.setMessage(accounts.getAccountID()+"_"+accounts.getName()+" want to register system with staff role");
            Accounts adminID = accountsRepository.findAdminByRoleID(1);
            notification.setAccountID(adminID.getAccountID());
            notificationRepository.save(notification);
            return notification;
    }
}
