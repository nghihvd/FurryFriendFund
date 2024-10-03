package org.example.furryfriendfund.notification;

import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.pets.Pets;

import java.util.List;

public interface INotificationService {
    Notification createRegisterNotification(Accounts accounts);
    Notification createNewPetNotification(Pets pets);
    boolean updateAccountStatusNotification(String notiID,boolean status);
    Notification adoptNotification(String accountID, String petID);
    Notification refuseAdoptRequestNotification(Appointments appointments, String reason);
    Notification acceptAdoptRequestNotification(Appointments appointments, String staffID);
    boolean updatePetsStatusNotification(String notiID,boolean status);
    List<Notification> showNotifications(int roleID);
    Notification findNoti(String notiID);
    Notification resultAdoptNotification(Appointments appointments, String status);
    List<Notification> showNotificationsAccountID(String accountID);
    Notification acceptNewPetNoti(String petID,String petName);
    public Notification denyNewPetNoti(String petID,String petName);
    public boolean deleteNoti(String notiID);
    Notification save(Notification notification);
}
