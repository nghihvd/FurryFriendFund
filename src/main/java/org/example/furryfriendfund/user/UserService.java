package org.example.furryfriendfund.user;

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
    @Override
    public void update(UsersDTO user) {
        //tìm kiếm user bằng id  nếu ko có thì quăng Exception để thông báo cho user
        userRepository.findById(user.getUserID()).orElseThrow();
        userRepository.save(user);// có thể tạo người dùng mới nhưng trong trường hợp id tồn tại thì sẽ cập nhật thay vì tạo
    }
}
