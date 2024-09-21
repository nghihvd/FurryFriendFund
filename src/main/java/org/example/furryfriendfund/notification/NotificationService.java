package org.example.furryfriendfund.notification;

import org.example.furryfriendfund.accounts.Accounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificationService implements INotificationService{

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void createNotification(Accounts accounts) {
        if(accounts.getRoleID() == 2){
            Notification notification = new Notification();
            notification.setMessage(accounts.getAccountID()+"_"+accounts.getName()+"want to regist system with staff role");

        }
    }
}
