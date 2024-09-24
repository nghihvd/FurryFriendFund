package org.example.furryfriendfund.notification;

import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.appointments.Appointments;

public interface INotificationService {
    public  Notification createRegisterNotification(Accounts accounts);
    boolean updateAccountStatusNotification(String notiID,boolean status);
    void adoptNotification(String accountID, String petID);
    void refuseAdoptRequestNotification(Appointments appointments, String reason);
    void acceptAdoptRequestNotification(Appointments appointments);
}
