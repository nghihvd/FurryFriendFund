package org.example.furryfriendfund.user;

import jdk.jshell.spi.ExecutionControl;
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
    public Users registerUser(Users user) {

        return userRepository.save(user);
    }

    @Override
    public Users getUserById(String userID) {
        // Tìm kiếm User theo userID
        if (userRepository.existsById(userID)) {
            return userRepository.findById(userID).get();
        }
        return null;
    }

    // Phương thức để kiểm tra thông tin đăng nhập
    @Override
    public boolean ckLogin(String userID,String password) {
        Users user = getUserById(userID);
        if(user == null){
            return false;
        }
        else {
            return user.getPassword().equals(password);
        }
    }
    @Override
    public void update(Users user) {
        //tìm kiếm user bằng id  nếu ko có thì quăng Exception để thông báo cho user
        userRepository.findById(user.getUserID()).orElseThrow();
        userRepository.save(user);// có thể tạo người dùng mới nhưng trong trường hợp id tồn tại thì sẽ cập nhật thay vì tạo
    }
}
