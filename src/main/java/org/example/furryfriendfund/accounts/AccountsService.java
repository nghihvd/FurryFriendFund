package org.example.furryfriendfund.accounts;

import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationService;
import org.example.furryfriendfund.pets.Pets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
//    @Autowired
//    private HttpServletResponse httpServletResponse;

    // yêu cầu Spring tìm kiếm một bean có kiểu dữ liệu la AccountsRepository để tiêm vào thuộc tính userReposistory
    @Override
    public Accounts saveAccountsInfo(Accounts user) {
        if(getAccByIdIgnoreCase(user.getAccountID()) != null){
            throw new DataIntegrityViolationException("Account already exists");
        } else{
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if(user.getRoleID() == 3){
                user.setNote("Available");
            } else if (user.getRoleID() == 2) {
                user.setNote("Waiting");
                notificationService.createRegisterNotification(user);
            }
        }
        return  userRepository.save(user);
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
            } else if(accounts.getNote().equals("Banned")) {
                message = "Account already banned";
            }
        }

        return message;

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
