package org.example.furryfriendfund.notification;

import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.accounts.AccountsRepository;
import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
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

    /**
     * tạo thông báo khi ng đăng kí chọn role là staff thì account sẽ được lưu vào
     * database nhưng mà note là Waiting thì sẽ chưa login được
     * @param accounts thông tin tài khoản đã đăng nhập
     * @return tạo ra thông báo với status là 1 và gửi cho all account có
     * role là admin
     */
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

    @Override
    public Notification createNewPetNotification(Pets pets) {
        Notification noti = new Notification();
        noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
        noti.setMessage(pets.getPetID()+"_"+pets.getName()+" can be added in our shelter??");
        noti.setRoleID(1);
        noti.setPetID(pets.getPetID());
        noti.setButton_status(true);
        notificationRepository.save(noti);
        return noti;
    }

    /**
     * khi admin bấm chấp nhận tài khoản vs role staff thì status của noti sẽ chuyển
     * thành 1 và note ở account sẽ là available là được login
     * @param notiID id thoong báo thay đổi trạng thái
     * @param status trạng thái mới thay đổi
     */
    @Override
    public boolean updateAccountStatusNotification(String notiID,boolean status) {
        boolean result = false;
        Notification noti = notificationRepository.findById(notiID).orElse(null);
               if(noti != null && status) {
                   noti.setStatus(status);
                   notificationRepository.save(noti);
                   String staffID = noti.getMessage().split("_")[0];
                   Accounts acc = accountsRepository.findById(staffID).orElse(null);
                   if(acc != null){
                       acc.setNote("Available");
                       accountsRepository.save(acc);
                       result = true;
                   }
               }
               return result ;
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
        String text = "Account "+acc.getName()+" has send a request adopt baby "+pet.getName()+".";
        Notification noti = new Notification();
        noti.setStatus(false);
        noti.setPetID(petID);
        noti.setNotiID(notiID);
        noti.setRoleID(2);
        noti.setMessage(text);
        notificationRepository.save(noti);


    }

    @Override
    public void refuseAdoptRequestNotification(Appointments appointments, String reason) {
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        Pets pet = petsRepository.findById(appointments.getPetID()).orElse(null);
        String text = "Your request adopt baby "+pet.getName()+" has been refused because: "+reason;
        Notification noti = new Notification();
        noti.setNotiID(notiID);
        noti.setAccountID(appointments.getAccountID());
        noti.setMessage(text);
        notificationRepository.save(noti);
    }

    @Override
    public void acceptAdoptRequestNotification(Appointments appointments, String staffID) {
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        Accounts acc = accountsRepository.findById(staffID).orElse(null);
        Pets pet = petsRepository.findById(appointments.getPetID()).orElse(null);
        String text ="Your request adopt baby "+pet.getName()+" has been accepted. " +
                "Please come to our address and meet staff "+acc.getName()+" on time: 123 NguyenVanA Q1 TPHCM";
        Notification noti = new Notification();
        noti.setNotiID(notiID);
        noti.setAccountID(appointments.getAccountID());
        noti.setMessage(text);
        notificationRepository.save(noti);
    }

    @Override
    public boolean updatePetsStatusNotification(String notiID, boolean status) {
        boolean result = false;
        Notification noti = notificationRepository.findById(notiID).orElse(null);
        if(noti != null && status) {
            noti.setStatus(status);
            noti.setButton_status(false);
            notificationRepository.save(noti);
            Pets pet = petsRepository.findById(noti.getPetID()).orElse(null);
            if(pet != null){
                pet.setStatus("Available");
                petsRepository.save(pet);
                result = true;
            }
        }
        return result ;
    }

    @Override
    public void resultAdoptNotification(Appointments appointments, String status) {
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        Accounts acc = accountsRepository.findById(appointments.getAccountID()).orElse(null);
        Pets pet = petsRepository.findById(appointments.getPetID()).orElse(null);
        String text = "Request adopt baby "+pet.getName()+" by account "+acc.getName()+" has been "+status+".";
        Notification noti = new Notification();
        noti.setNotiID(notiID);
        noti.setRoleID(1);
        noti.setMessage(text);
        notificationRepository.save(noti);
    }

    @Override
    public List<Notification> showNotifications(int roleID) {
        return notificationRepository.findByroleID(roleID);
    }

    @Override
    public Notification findNoti(String notiID){

        return notificationRepository.findById(notiID).orElse(null);
    }
    @Override
    public List<Notification> showNotificationsAccountID(String accountID){
        return notificationRepository.findByAccountID(accountID);
    }



}
