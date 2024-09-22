package org.example.furryfriendfund.notification;

import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.accounts.AccountsRepository;
import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.UUID;

@Service
public class NotificationService implements INotificationService{

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private PetsRepository petsRepository;

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

    public void updateAccountStatusNotification(String notiID,boolean status) {
        Notification noti = notificationRepository.findById(notiID).orElse(null);
               if(noti != null && status == true) {
                   noti.setStatus(status);
                   notificationRepository.save(noti);
                   String staffID = noti.getMessage().split("_")[0];
                   Accounts acc = accountsRepository.findById(staffID).orElse(null);
                   if(acc != null){
                       acc.setNote("Available");
                       accountsRepository.save(acc);
                   }
               }

    }

    /**
     * Tạo thông báo khi có yêu cầu nhận nuôi
     * @param accountID
     * @param petID
     */
    @Override
    public void adoptNotification(String accountID, String petID) {
        Pets pet = petsRepository.findById(petID).orElse(null);
        Accounts acc = accountsRepository.findById(accountID).orElse(null);
        Random rand = new Random();
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        if(pet != null && acc != null){
            String text = "Account "+acc.getName()+" has send a request adopt baby "+pet.getName()+".";
            Notification noti = new Notification();
            noti.setStatus(false);
            noti.setPetID(petID);
            noti.setNotiID(notiID);
            noti.setRoleID(2);
            noti.setMessage(text);
            notificationRepository.save(noti);
        }

    }

}
