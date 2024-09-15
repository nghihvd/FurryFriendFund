package org.furryFriendFund.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService implements IUsersService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void update(UsersDTO user) {
        //tìm kiếm user bằng id  nếu ko có thì quăng Exception để thông báo cho user
        userRepository.findById(user.getUserID()).orElseThrow();
        userRepository.save(user);// có thể tạo người dùng mới nhưng trong trường hợp id tồn tại thì sẽ cập nhật thay vì tạo
    }
}
