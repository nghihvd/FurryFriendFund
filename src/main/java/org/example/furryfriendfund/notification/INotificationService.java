package org.example.furryfriendfund.notification;

import org.example.furryfriendfund.accounts.Accounts;

public interface INotificationService {
    public  Notification createRegisterNotification(Accounts accounts);
    boolean updateAccountStatusNotification(String notiID,boolean status);
    void adoptNotification(String accountID, String petID);
}