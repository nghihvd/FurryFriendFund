package org.example.furryfriendfund.accounts;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
// đánh dấu lớp service xử lý logic được quản lý bởi spring
// và sẽ ta ra các class cần cho việc quản lý lớp Service
public class AccountsService implements IAccountsService {

    @Autowired
    private AccountsRepository userRepository;
//    @Autowired
//    private HttpServletResponse httpServletResponse;

    // yêu cầu Spring tìm kiếm một bean có kiểu dữ liệu la AccountsRepository để tiêm vào thuộc tính userReposistory
    @Override
    public Accounts saveAccountsInfo(Accounts user) {
        return userRepository.save(user);
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

    /**
     * xóa hết thông tin đăng nhập của người đăng nhập
     */
    @Override
    public void logout(){

    }

    @Override
    public void update(Accounts user) {
        //tìm kiếm accounts bằng id  nếu ko có thì quăng Exception để thông báo cho accounts
        userRepository.findById(user.getAccountID()).orElseThrow();
        userRepository.save(user);// có thể tạo người dùng mới nhưng trong trường hợp id tồn tại thì sẽ cập nhật thay vì tạo
    }
}
