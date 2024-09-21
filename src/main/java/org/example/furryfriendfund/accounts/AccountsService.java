package org.example.furryfriendfund.accounts;

import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
// đánh dấu lớp service xử lý logic được quản lý bởi spring
// và sẽ ta ra các class cần cho việc quản lý lớp Service
public class AccountsService implements IAccountsService {

    @Autowired
    private AccountsRepository userRepository;
    @Autowired
    private NotificationService notificationService;
//    @Autowired
//    private HttpServletResponse httpServletResponse;

    // yêu cầu Spring tìm kiếm một bean có kiểu dữ liệu la AccountsRepository để tiêm vào thuộc tính userReposistory
    @Override
    public Accounts saveAccountsInfo(Accounts user) {
        Accounts newAccount = null;
        if(getUserById(user.getAccountID()) != null){
            throw new DataIntegrityViolationException("Account already exists");
        }
        if(user.getRoleID() == 2){
            Notification noti = notificationService.createRegisterNotification(user);
            if(noti != null &&  true == noti.isStatus()){
                newAccount = userRepository.save(user);
            }
        }
        return newAccount;
    }

    @Override
    public Accounts getUserById(String userID) {
        // Tìm kiếm User theo userID
        if (userRepository.existsById(userID)) {
            return userRepository.findById(userID).get();
        }
        return null;
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



}
