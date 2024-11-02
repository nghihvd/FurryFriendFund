package org.example.furryfriendfund.notification;

import jakarta.annotation.PostConstruct;
import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.accounts.AccountsRepository;
import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.events.Events;
import org.example.furryfriendfund.events.EventsRepository;
import org.example.furryfriendfund.pet_health_records.Pet_health_record;
import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService implements INotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private PetsRepository petsRepository;
    @Autowired
    private EventsRepository eventsRepository;


    /**
     * tạo thông báo khi ng đăng kí chọn role là staff thì account sẽ được lưu vào
     * database nhưng mà note là Waiting thì sẽ chưa login được
     *
     * @param accounts thông tin tài khoản đã đăng nhập
     * @return tạo ra thông báo với status là 1 và gửi cho all account có
     * role là admin
     */
    @Override
    public Notification createRegisterNotification(Accounts accounts) {
        Notification notification = new Notification();
        notification.setNotiID(UUID.randomUUID().toString().substring(0, 8));
        notification.setMessage(accounts.getAccountID() + "_" + accounts.getName() + " want to register system with staff role");
        notification.setButton_status(true);
        notification.setRoleID(1);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return notification;
    }

    @Override
    public Notification createNewPetNotification(Pets pets) {
        Notification noti = new Notification();
        noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
        noti.setMessage(pets.getPetID() + "_" + pets.getName() + " can be added to our shelter??" +
                "\nID: " + pets.getPetID() +
                "\nName: " + pets.getName() +
                "\nAccount ID: " + pets.getAccountID() +
                "\nBreed: " + pets.getBreed() +
                "\nSex: " + pets.getSex() +
                "\nAge: " + pets.getAge() +
                "\nWeight: " + pets.getWeight() +
                "\nStatus: " + pets.getStatus() +
                "\nNote: " + pets.getNote() +
                "\nSize: " + pets.getSize() +
                "\nPotty Trained: " + (pets.isPotty_trained() ? "Yes" : "No") +
                "\nDietary Requirements: " + (pets.isDietary_requirements() ? "Yes" : "No") +
                "\nSpayed: " + (pets.isSpayed() ? "Yes" : "No") +
                "\nVaccinated: " + (pets.isVaccinated() ? "Yes" : "No") +
                "\nSocialized: " + (pets.isSocialized() ? "Yes" : "No") +
                "\nRabies Vaccinated: " + (pets.isRabies_vaccinated() ? "Yes" : "No") +
                "\nOrigin: " + pets.getOrigin() +
                "\nImage URL: " + pets.getImg_url() +
                "\nCategory ID: " + pets.getCategoryID() +
                "\nAdopt Date: " + pets.getAdopt_date() +
                "\nDescription: " + pets.getDescription());

        noti.setRoleID(1);
        noti.setPetID(pets.getPetID());
        noti.setButton_status(true);
        noti.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(noti);
        return noti;
    }

    @Override
    public Notification eventStatusNotification(Events events) {
        Notification noti = new Notification();
        noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
        noti.setMessage(events.getEventID() + "_" + events.getEvent_name() + "_"
                + "is an event which staff want to accept "
                + "\nID: " + events.getEventID()
                + "\nName: " + events.getEvent_name()
                + "\nStart Date: " + events.getStart_date()
                + "\nEnd Date: " + events.getEnd_date()
                + "\nDescription: " + events.getDescription()
                + "\nImage URL: " + events.getImg_url()
                + "\nStatus: " + events.getStatus());
        noti.setButton_status(true);
        noti.setRoleID(1);
        noti.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(noti);
    }

    @Override
    public Notification changeStatusNotification(Accounts accounts, String status) {
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        String text = "Your account have been change status to " + status;
        Notification noti = new Notification();
        noti.setAccountID(accounts.getAccountID() );
        noti.setNotiID(notiID);
        noti.setRoleID(accounts.getRoleID());
        noti.setMessage(text);
        noti.setCreatedAt(LocalDateTime.now());
        noti.setButton_status(false);
        notificationRepository.save(noti);
        return noti;
    }

    /**
     * automated delete notification which have time create more than 2 weeks
     */

    @Override
    @Transactional
    @Scheduled(cron = "0 0 00 * * ?")
    public void deleteOldNoti() {
        LocalDateTime twoWeekAgo = LocalDateTime.now().minus(14, ChronoUnit.DAYS);
        Timestamp ts = Timestamp.valueOf(twoWeekAgo);
        System.out.println(ts);
        int count = notificationRepository.deleteByCreatedAtBefore(ts);
        System.out.println(count);
    }


    @PostConstruct
    public void init(){
        deleteOldNoti();
    }

    @Override
    public List<Notification> getEventNoti() {
        return notificationRepository
                .findAll()
                .stream()
                .filter(notievent ->notievent.getMessage().contains("is an event which staff want to accept"))
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                // Lọc để lấy notification mới nhất cho mỗi event
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(this::extractEventIdFromMessage),
                        map -> map.values().stream()
                                .map(list -> list.get(0))
                                .collect(Collectors.toList())
                ));
    }

    @Override
    public boolean deleteNotificationAboutPetID(String petID) {
        List<Notification> search = notificationRepository.getNotificationByPetID(petID);
        notificationRepository.deleteAll(search);
        List<Notification> all = notificationRepository.findAll();
        return !all.containsAll(search);
    }

    @Override
    public Notification createDeletePetRequestNotification(String petID) {
        Notification noti = new Notification();
        List<Notification> notis = notificationRepository.getNotificationByPetID(petID);
        for(Notification n : notis) {
            if (n.getMessage().contains("can be deleted?")){
                noti = n;
            }
        }
        if (noti.getNotiID() == null) {
            noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
            Pets pet = petsRepository.getPetByPetID(petID);
            noti.setMessage(pet.getPetID() + "_" + pet.getName() + " can be deleted??");
            noti.setRoleID(1);
            noti.setPetID(pet.getPetID());
            noti.setButton_status(true);
            noti.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(noti);
        }
        return noti;

    }

    @Override
    public List<Notification> getNotificationByPetID(String petID) {
        return notificationRepository.getNotificationByPetID(petID);
    }

    public String extractEventIdFromMessage(Notification notification) {
        // Lấy dòng đầu tiên của message
        String firstLine = notification.getMessage().split("\n")[0];
        // Lấy eventID từ phần đầu của dòng đầu tiên
        //return ra eventID
        return firstLine.split("_")[0];
    }




    /**
     * khi admin bấm chấp nhận tài khoản vs role staff thì status của noti sẽ chuyển
     * thành 1 và note ở account sẽ là available là được login
     * @param notiID id thoong báo thay đổi trạng thái
     * @param status trạng thái mới thay đổi
     */
    @Override
    public boolean updateAccountStatusNotification(String notiID, boolean status) {
        boolean result = false;
        Notification noti = notificationRepository.findById(notiID).orElse(null);
        String staffID = noti.getMessage().split("_")[0];
        Accounts acc = accountsRepository.findById(staffID).orElse(null);
        if (status) {
            noti.setStatus(true);
            noti.setButton_status(false);
            notificationRepository.save(noti);
            if (acc != null) {
                acc.setNote("Available");
                accountsRepository.save(acc);
                result = true;
            }
        } else{
            if(acc != null) {
                notificationRepository.delete(noti);
                accountsRepository.delete(acc);
                result = true;
            }
        }

        return result;
    }

    /**
     * Tạo thông báo khi có yêu cầu nhận nuôi
     *
     * @param accountID
     * @param petID
     */
    @Override
    public Notification adoptNotification(String accountID, String petID) {
        Pets pet = petsRepository.findById(petID).orElse(null);
        Accounts acc = accountsRepository.findById(accountID).orElse(null);
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        String text = "Account " + acc.getName() + " has send a request adopt baby " + pet.getName() + ".";
        Notification noti = new Notification();
        noti.setStatus(false);
        noti.setPetID(petID);
        noti.setNotiID(notiID);
        noti.setRoleID(2);
        noti.setMessage(text);
        noti.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(noti);
        return noti;
    }

    @Override
    public Notification refuseAdoptRequestNotification(Appointments appointments, String reason) {
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        Pets pet = petsRepository.findById(appointments.getPetID()).orElse(null);
        String text = "Your request adopt baby " + pet.getName() + " has been refused because: " + reason;
        Notification noti = new Notification();
        noti.setNotiID(notiID);
        noti.setAccountID(appointments.getAccountID());
        noti.setMessage(text);
        noti.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(noti);
        return noti;
    }

    @Override
    public Notification acceptAdoptRequestNotification(Appointments appointments, String staffID) {
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        Accounts acc = accountsRepository.findById(staffID).orElse(null);
        Pets pet = petsRepository.findById(appointments.getPetID()).orElse(null);
        String text = "Your request adopt baby " + pet.getName() + " has been accepted. " +
                "Please come to our address and meet staff " + acc.getName() + " on time: FPTU-HCM";
        Notification noti = new Notification();
        noti.setNotiID(notiID);
        noti.setAccountID(appointments.getAccountID());
        noti.setMessage(text);
        noti.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(noti);
        return noti;
    }

    @Override
    public Notification remindReportNotification(Pets pets) {
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        String text = "Please report us about baby "+pets.getName()+". If you do not report soon, we will consider you have violated our policy. Thanks!";
        Notification noti = new Notification();
        noti.setNotiID(notiID);
        noti.setAccountID(pets.getAccountID());
        noti.setCreatedAt(LocalDateTime.now());
        noti.setMessage(text);
        notificationRepository.save(noti);
        return noti;
    }
    @Override
    public Notification banRequestNotification(Pets pets, Accounts staff){
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        String text = "Staff "+staff.getName()+" sent a request ban account "+pets.getAccountID()+" because do not report baby "+pets.getName()+"'s status.";
        Notification noti = new Notification();
        noti.setNotiID(notiID);
        noti.setButton_status(true);
        noti.setPetID(pets.getPetID());
        noti.setRoleID(1);
        noti.setCreatedAt(LocalDateTime.now());
        noti.setMessage(text);
        notificationRepository.save(noti);
        return noti;
    }


    @Override
    public boolean updatePetsStatusNotification(String notiID, boolean status) {
        boolean result = false;
        Notification noti = notificationRepository.findById(notiID).orElse(null);
        if (noti != null && status) {
            noti.setStatus(true);
            noti.setButton_status(false);
            notificationRepository.save(noti);
            Pets pet = petsRepository.findById(noti.getPetID()).orElse(null);
            if (pet != null) {
                pet.setStatus("Available");
                petsRepository.save(pet);
                result = true;
            }
        }

        return result;
    }

    @Override
    public Notification resultAdoptNotification(Appointments appointments, String status) {
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        Accounts acc = accountsRepository.findById(appointments.getAccountID()).orElse(null);
        Pets pet = petsRepository.findById(appointments.getPetID()).orElse(null);
        String text = "Request adopt baby " + pet.getName() + " by account " + acc.getName() + " has been " + status + ".";
        Notification noti = new Notification();
        noti.setNotiID(notiID);
        noti.setRoleID(1);
        noti.setMessage(text);
        noti.setCreatedAt(LocalDateTime.now());
        return noti;
    }

    @Override
    public Notification cancelAppointmentNotification(Appointments appointments) {
        String notiID = UUID.randomUUID().toString().substring(0, 8);
        Pets pet = petsRepository.findById(appointments.getPetID()).orElse(null);
        String text = "Request adopt baby " + pet.getName() + " has been canceled";
        Notification noti = new Notification();
        noti.setNotiID(notiID);
        noti.setAccountID(appointments.getStaffID());
        noti.setMessage(text);
        noti.setCreatedAt(LocalDateTime.now());
        return noti;
    }

    @Override
    public List<Notification> getBanRequestNotifications(){
        List<Notification> list = new ArrayList<>();
        for (Notification n : showNotifications(1)) {
            if (n.isButton_status() && n.getMessage().contains("sent a request ban account")) {
                list.add(n);
            }
        }
        return list;
    }

    @Override
    public List<Notification> showNotifications(int roleID) {
        return notificationRepository.findByRoleIDOrderByCreatedAtDesc(roleID);
    }

    @Override
    public Notification findNoti(String notiID) {
        return notificationRepository.findById(notiID).orElse(null);
    }

    @Override
    public List<Notification> showNotificationsAccountID(String accountID) {
        return notificationRepository.findByAccountIDOrderByCreatedAtDesc(accountID);
    }

    @Override
    public Notification acceptNewPetNoti(String petID, String petName) {
        Notification noti = new Notification();
        noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
        noti.setRoleID(2);
        noti.setMessage(petID + "_" + petName + "has been accepted.");
        noti.setButton_status(false);
        noti.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(noti);
    }

    @Override
    public Notification denyNewPetNoti(String petID, String petName) {
        Notification noti = new Notification();
        noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
        noti.setRoleID(2);
        noti.setMessage(petID + "_" + petName + " has been denied.");
        noti.setButton_status(false);
        noti.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(noti);
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Notification createHealthNoti(Pet_health_record record) {
        Notification noti = new Notification();
        noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
        noti.setRoleID(1);
        noti.setMessage(record.getPetID() + " is added new health record:" +
                "\nRecord ID: " + record.getRecordID() + "\nCheck out date:" + record.getCheck_out_date() +
                "\nCheck in date: " + record.getCheck_in_date() +
                "\nVeterinarian name: " + record.getVeterinarian_name() +
                "\nVeterinary fee: " + record.getVeterinary_fee() +
                "\nIlness name: " + record.getIllness_name() +
                "\nNote: " + record.getNote());
        noti.setButton_status(false);
        noti.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(noti);
    }

    @Override
    public Notification updateHealthNoti(Pet_health_record record) {
        Notification noti = new Notification();
        noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
        noti.setRoleID(1);
        noti.setMessage(record.getPetID() + " is updated health record:" +
                "\nCheck out date:" + record.getCheck_out_date() +
                "\nCheck in date: " + record.getCheck_in_date() +
                "\nVeterinarian name: " + record.getVeterinarian_name() +
                "\nVeterinary fee: " + record.getVeterinary_fee() +
                "\nIlness name: " + record.getIllness_name() +
                "\nNote: " + record.getNote());
        noti.setButton_status(false);
        noti.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(noti);
    }
    @Override
    public Notification updatePetNoti(Pets pets) {
        Notification noti = new Notification();
        noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
        noti.setRoleID(1);
        noti.setMessage(pets.getPetID() + " is updated:"+
                "\nID: " + pets.getPetID() +
                "\nName: " + pets.getName() +
                "\nAccount ID: " + pets.getAccountID() +
                "\nBreed: " + pets.getBreed() +
                "\nSex: " + pets.getSex() +
                "\nAge: " + pets.getAge() +
                "\nWeight: " + pets.getWeight() +
                "\nStatus: " + pets.getStatus() +
                "\nNote: " + pets.getNote() +
                "\nSize: " + pets.getSize() +
                "\nPotty Trained: " + (pets.isPotty_trained() ? "Yes" : "No") +
                "\nDietary Requirements: " + (pets.isDietary_requirements() ? "Yes" : "No") +
                "\nSpayed: " + (pets.isSpayed() ? "Yes" : "No") +
                "\nVaccinated: " + (pets.isVaccinated() ? "Yes" : "No") +
                "\nSocialized: " + (pets.isSocialized() ? "Yes" : "No") +
                "\nRabies Vaccinated: " + (pets.isRabies_vaccinated() ? "Yes" : "No") +
                "\nOrigin: " + pets.getOrigin() +
                "\nImage URL: " + pets.getImg_url() +
                "\nCategory ID: " + pets.getCategoryID() +
                "\nAdopt Date: " + pets.getAdopt_date() +
                "\nDescription: " + pets.getDescription());

        noti.setButton_status(false);
        noti.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(noti);
    }

    @Override
    public Notification deleteHealthNoti(Pet_health_record record) {
        Notification noti = new Notification();
        noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
        noti.setRoleID(1);
        noti.setMessage("Health record " + record.getPetID() + " has been deleted:" + "\nCheck out date:" + record.getCheck_out_date() +
                "\nCheck in date: " + record.getCheck_in_date() +
                "\nVeterinarian name: " + record.getVeterinarian_name() +
                "\nVeterinary fee: " + record.getVeterinary_fee() +
                "\nIlness name: " + record.getIllness_name() +
                "\nNote: " + record.getNote());
        noti.setButton_status(false);
        noti.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(noti);
    }

    @Override
    public List<Notification> showRegisNoti() {
        List<Notification> list = new ArrayList<>();
        for (Notification n : showNotifications(1)) {
            if (n.isButton_status() && n.getMessage().contains(" want to register system with staff role")) {
                list.add(n);
            }
        }
        return list;
    }


    @Override
    public boolean deleteNoti(String notiID) {
        notificationRepository.deleteById(notiID);
        return findNoti(notiID) == null;
    }


}
