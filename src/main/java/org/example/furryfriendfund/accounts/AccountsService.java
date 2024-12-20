package org.example.furryfriendfund.accounts;

import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationRepository;

import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.appointments.AppointmentsRepository;
import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
// đánh dấu lớp service xử lý logic được quản lý bởi spring
// và sẽ ta ra các class cần cho việc quản lý lớp Service
public class AccountsService implements IAccountsService, UserDetailsService {

    @Autowired
    private AccountsRepository userRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private AppointmentsRepository appointmentsRepository;
//    @Autowired
//    private HttpServletResponse httpServletResponse;

    // yêu cầu Spring tìm kiếm một bean có kiểu dữ liệu la AccountsRepository để tiêm vào thuộc tính userReposistory
    @Override
    public Accounts saveAccountsInfo(Accounts user) {
        Accounts fin = accountsRepository.findByAccountIDIgnoreCase(user.getAccountID());

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if(user.getRoleID() == 3){
                user.setNote("Waiting");
            } else if (user.getRoleID() == 2) {
                user.setNote("Waiting");
                notificationService.createRegisterNotification(user);
            }

        return  userRepository.save(user);
    }
    @Override
    public boolean banAccept(String accountID) {
        Accounts account = accountsRepository.findById(accountID).orElse(null);
        if (account != null) {
            Notification notification = notificationService.changeStatusNotification(account,"Banned");
            account.setNote("Banned");
            accountsRepository.save(account);
            List<Notification> accountNotifications = notificationRepository.findAll()
                    .stream()
                    .filter(noti -> noti.getMessage().contains(accountID))
                    .collect(Collectors.toList());
            List<String> appointmentIdsToDelete = accountNotifications.stream()
                    .map(noti -> notificationService.extractAppointmentIdFromMessage(noti))
                    .filter(appointmentId -> appointmentId != null && !appointmentId.isEmpty())
                    .collect(Collectors.toList());
            List<Notification> notificationsToDelete = accountNotifications.stream()
                    .filter(noti -> appointmentIdsToDelete.contains(notificationService.extractAppointmentIdFromMessage(noti)))
                    .collect(Collectors.toList());
            if (!notificationsToDelete.isEmpty()) {
                Notification latestNoti = notificationsToDelete.get(0);
                latestNoti.setStatus(false);
                notificationRepository.save(latestNoti);
            }
            notificationRepository.deleteAll(notificationsToDelete);
            for (String appointmentId : appointmentIdsToDelete) {
                try {
                    appointmentsRepository.deleteById(appointmentId);
                } catch (Exception e) {
                    System.err.println("Failed to delete appointment with ID: " + appointmentId);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean notBanAccept(String accountID) {
        List<Notification> accountNotifications = notificationRepository.findAll()
                .stream()
                .filter(noti -> noti.getMessage().contains(accountID))
                .collect(Collectors.toList());
        List<String> appointmentIdsToDelete = accountNotifications.stream()
                .map(noti -> notificationService.extractAppointmentIdFromMessage(noti))
                .filter(appointmentId -> appointmentId != null && !appointmentId.isEmpty())
                .collect(Collectors.toList());
        List<Notification> notificationsToDelete = accountNotifications.stream()
                .filter(noti -> appointmentIdsToDelete.contains(notificationService.extractAppointmentIdFromMessage(noti)))
                .collect(Collectors.toList());
        if (!notificationsToDelete.isEmpty()) {
            Notification latestNoti = notificationsToDelete.get(0);
            latestNoti.setStatus(false);
            notificationRepository.save(latestNoti);
        }
        notificationRepository.deleteAll(notificationsToDelete);
        for (String appointmentId : appointmentIdsToDelete) {
            try {
                appointmentsRepository.deleteById(appointmentId);
            } catch (Exception e) {
                System.err.println("Failed to delete appointment with ID: " + appointmentId);
            }
            return true;
        }
        return false;
    }

    @Override
    public int countRoleAccount(int roleID) {
        return accountsRepository.countRoleAccounts(roleID);
    }

    @Override
    public int countNote(String note) {
        return accountsRepository.countAccountsNote(note);
    }

    @Override
    public boolean checkBannAcc(String accID) {
        Accounts account = accountsRepository.findById(accID).orElse(null);
        boolean result = false;
        if (account != null) {
            if(account.getNote().equals("Banned")){
                result = true;
            }
        }
        return result;
    }

    @Override
    public Accounts getUserById(String userID) {
        // Tìm kiếm User theo userID
        if (userRepository.existsById(userID)) {
            return userRepository.findById(userID).get();
        }
        return null;
    }



    @Override
    public Accounts save(Accounts accounts) {
        return userRepository.save(accounts);
    }

    @Override
    public boolean delete(Accounts accounts) {
        accountsRepository.delete(accounts);
        return !accountsRepository.existsById(accounts.getAccountID());
    }

    @Override
    public List<Accounts> showDonators() {
        return accountsRepository.showDonator();
    }

    @Override
    public Accounts getAccByIdIgnoreCase(String id) {
       return accountsRepository.findByAccountIDIgnoreCase(id);
    }

    @Override
    public List<Accounts> getAllAccounts() {
        return accountsRepository.findAll();
    }


    /**
     *
     * search = status, role, name, accountID
     */
    @Override
    public List<Accounts> searchAccByName(String name, int role, String accountID, String note) {
        List<Accounts> accounts;
        if (!accountID.isEmpty()) {
            accounts = accountsRepository.findByAccountIDContainingIgnoreCase(accountID);
        } else if (!name.isEmpty()) {
            accounts = accountsRepository.findByNameContainingIgnoreCase(name);
        } else {
            accounts = accountsRepository.findAll();
        }
        return filterByRoleAndNote(accounts, role, note);
    }

    private List<Accounts> filterByRoleAndNote(List<Accounts> accounts, int role, String note) {
        List<Accounts> filteredAccounts = new ArrayList<>();
        for (Accounts account : accounts) {
            boolean matches = true;
            // Check role
            if (role != 0 && account.getRoleID() != role) {
                matches = false;
            }

            // Check note
            if (!note.isEmpty() &&
                    (account.getNote() == null ||
                            !account.getNote().toLowerCase().contains(note.toLowerCase()))) {
                matches = false;
            }

            if (matches) {
                filteredAccounts.add(account);
            }
        }
        return filteredAccounts;
    }


    @Override
    public String ChangeStatus(Accounts accounts,String button) {
        String message = null;

        if(button.equals("Upgrade")){
            if(accounts.getRoleID() == 2) {
                accounts.setRoleID(1);
                accountsRepository.save(accounts);
                message = "Account upgraded to admin";
                notificationService.changeStatusNotification(accounts, "admin");
                System.out.println(notificationService.changeStatusNotification(accounts, "admin"));
            }
            if(accounts.getRoleID() == 3){
                accounts.setRoleID(2);
                accountsRepository.save(accounts);
                message = "Account upgraded to staff";
                notificationService.changeStatusNotification(accounts, "staff");
            }

        }
        if(button.equals("Enable")){
            if(accounts.getNote().equals("Banned") || accounts.getNote().equals("Waiting")) {
                accounts.setNote("Available");
                accountsRepository.save(accounts);
                message = "Account is available";
            } else{
                message = "Account already available";
            }
        }
        if(button.equals("Disable")){

            if(accounts.getRoleID() == 2 || accounts.getRoleID() == 1){
                accounts.setRoleID(3);
                accountsRepository.save(accounts);
                message = "Account is changed to be member";
                notificationService.changeStatusNotification(accounts, "member");
            } else if(accounts.getRoleID() == 3 && (accounts.getNote().equals("Waiting") || accounts.getNote().equals("Available"))) {
                accounts.setNote("Banned");
                accountsRepository.save(accounts);
                message = "Account banned";
            }
            else if(accounts.getNote().equals("Banned")) {
                message = "Account already banned";
            }
        }

        return message;

    }

    @Override
    public boolean changeStatusAcc(String accID) {
        Accounts acc = getUserById(accID);
        boolean re = false;
        if(acc != null){
            acc.setNote("Available");
            accountsRepository.save(acc);
            re = true;
        }
         return re;
    }

    @Override
    public boolean Verify(String accID, boolean married, String job, int income, String citizen_serial, boolean experience_caring,String confirm_address) {
        Accounts acc = getUserById(accID);
        boolean re = false;
        if(acc != null){
            acc.setExperience_caring(experience_caring);
            acc.setConfirm_address(confirm_address);
            acc.setMarried(married);
            acc.setJob(job);
            acc.setIncome(income);
            acc.setCitizen_serial(citizen_serial);
            re = true;
            accountsRepository.save(acc);
        }
        return re;
    }

    /**
     * Phương thức để kiểm tra thông tin đăng nhập
     * @param accountID
     * @param password
     * @return
     */
    @Override
    public boolean ckLogin(String accountID,String password) {
        Accounts accounts = getUserById(accountID);
        if(accounts == null){
            return false;
        }
        else {
            if(accounts.getPassword().equals(password)){

                return true;
            }else{
                return false;
            }
        }
    }

    /**
     * loadUserByUsername from interface UserDetailService
     * @param username is accountID that user login
     * @return LoggerDetail include information about account and authority
     * @throws UsernameNotFoundException  if cannot find accountID in database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Accounts acc = this.getUserById(username);
        if(acc == null){
            throw new UsernameNotFoundException("Account not found");
        }
        return new LoggerDetail(acc);
    }

}
