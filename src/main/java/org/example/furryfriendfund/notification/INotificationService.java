package org.example.furryfriendfund.notification;

import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.events.Events;
import org.example.furryfriendfund.pet_health_records.Pet_health_record;
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
     Notification denyNewPetNoti(String petID,String petName);
     boolean deleteNoti(String notiID);
    Notification save(Notification notification);
    Notification createHealthNoti(Pet_health_record record);
    Notification updateHealthNoti(Pet_health_record record);
    Notification deleteHealthNoti(Pet_health_record record);
    List<Notification> showRegisNoti();
    Notification createEventsNoti(Events events);

}
