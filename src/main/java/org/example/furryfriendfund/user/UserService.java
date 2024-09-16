package org.example.furryfriendfund.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
// đánh dấu lớp service xử lý logic được quản lý bởi spring
// và sẽ ta ra các class cần cho việc quản lý lớp Service
public class UserService implements IUsersService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpServletResponse httpServletResponse;

    // yêu cầu Spring tìm kiếm một bean có kiểu dữ liệu la UserRepository để tiêm vào thuộc tính userReposistory
    @Override
    public Users saveUser(Users user) {

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
            if(user.getPassword().equals(password)){
                Cookie cookie = new Cookie("userID",userID);
                cookie.setMaxAge(60*60); // cookies sẽ hết trong vòng 1 tiếng
                cookie.setSecure(true);//cookie chỉ được gửi qua HTTPS
                cookie.setHttpOnly(true);//ngăn chặn JS truy cập trực tiếp vào content cookies

                httpServletResponse.addCookie(cookie);//add cookies vô response để gửi phản hồi đến client
                return true;
            }else{
                return false;
            }
        }
    }

    //đăng xuất khi user người dùng
    @Override
    public void logout(){
        Cookie cookie = new Cookie("userID",null);
        cookie.setMaxAge(0);//xóa cookie
        httpServletResponse.addCookie(cookie);
    }


}
