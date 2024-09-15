package org.furryFriendFund.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// đánh dấu lớp service xử lý logic được quản lý bởi spring
// và sẽ ta ra các class cần cho việc quản lý lớp Service
public class UserService implements IUsersService {

    @Autowired
    private UserRepository userRepository;

    // yêu cầu Spring tìm kiếm một bean có kiểu dữ liệu la UserRepository để tiêm vào thuộc tính userReposistory
    @Override
    public UsersDTO registerUser(UsersDTO user) {
        return userRepository.save(user);
    }

    public UsersDTO getUserById(String userID) {
        // Tìm kiếm User theo userID
        if (userRepository.existsById(userID)) {
            return userRepository.findById(userID).get();
        }
        return null;
    }

    // Phương thức để kiểm tra thông tin đăng nhập
    public boolean login(String userID) {
        if(getUserById(userID) != null) {
            return true;
        }else{
            return false;
        }
    }

}
