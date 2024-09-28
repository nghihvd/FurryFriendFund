package org.example.furryfriendfund.notification;

import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.pets.Pets;

public interface INotificationService {
    Notification createRegisterNotification(Accounts accounts);
    Notification createNewPetNotification(Pets pets);
    boolean updateAccountStatusNotification(String notiID,boolean status);
    void adoptNotification(String accountID, String petID);
    void refuseAdoptRequestNotification(Appointments appointments, String reason);
    void acceptAdoptRequestNotification(Appointments appointments, String staffID);
    boolean updatePetsStatusNotification(String notiID,boolean status);
}
