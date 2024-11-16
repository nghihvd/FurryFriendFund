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
    Notification remindReportNotification(Pets pets);
    Notification banRequestNotification(Pets pets, Accounts staff);
    List<Notification> getBanRequestNotifications();
    boolean updatePetsStatusNotification(String notiID,boolean status);
    List<Notification> showNotifications(int roleID);
    Notification findNoti(String notiID);
    Notification resultAdoptNotification(Appointments appointments, String status);
    Notification cancelAppointmentNotification(Appointments appointments);
    List<Notification> showNotificationsAccountID(String accountID);
    Notification acceptNewPetNoti(String petID,String petName);
     Notification denyNewPetNoti(String petID,String petName);
     boolean deleteNoti(String notiID);
    Notification save(Notification notification);
    Notification createHealthNoti(Pet_health_record record);
    Notification updateHealthNoti(Pet_health_record record);
    Notification deleteHealthNoti(Pet_health_record record);
    List<Notification> showRegisNoti();
    Notification eventStatusNotification(Events events);
    Notification changeStatusNotification(Accounts accounts, String status);
    void deleteOldNoti();
    List<Notification> getEventNoti();
    boolean deleteNotificationAboutPetID(String petID);
    Notification createDeletePetRequestNotification(String petID);
    List<Notification> getNotificationByPetID(String petID);
    Notification updatePetNoti(Pets pets);
    Notification requestTrustNotification(Appointments appointments);
    List<Notification> getTrustRequestNotifications();
    Notification refuseTrustRequestNotifications (String staffID, Pets pet);
    Notification acceptTrustRequestNotificationsForMember (String accountID, Pets pet);
    Notification acceptTrustRequestNotificationsForStaff (String staffID, Pets pet);
    Notification createNotificationNoTrustForAdmin(String appointID,String accountID, String reason);
    Notification createNotificationReturnPetRequest(String petID, String accountID, String reason);
    Notification findByMessage(String appointment);
    List<Notification> findReturnNotification();
    boolean checkExistTrustRequest(String petID);

}
