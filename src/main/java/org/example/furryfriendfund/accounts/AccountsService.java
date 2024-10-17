package org.example.furryfriendfund.accounts;

import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


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
        if(getUserById(user.getAccountID()) != null){
            throw new DataIntegrityViolationException("Account already exists");
        } else{
            System.out.println(passwordEncoder.encode(user.getPassword()));
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
