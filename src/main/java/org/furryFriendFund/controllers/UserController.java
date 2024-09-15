package org.furryFriendFund.controllers;

import org.furryFriendFund.user.UserRepository;
import org.furryFriendFund.user.UserService;
import org.furryFriendFund.user.IUsersService;
import org.furryFriendFund.user.UsersService;
import org.furryFriendFund.user.UsersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
// đánh dấu class là 1 controller (xử lý yêu cầu HTTP từ client, kết hợp với @ResponseBody để trả về
// định dạng JSON)
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UsersService usersDAO;
    private UserService userService;

    //oldPassword kiểm tra thông tin mật khẩu người dùng có nhập nếu đúng thì mới cho nhập
    @PutMapping("/update/{oldPassword}")
    public String updateUser(@RequestBody UsersDTO newUser, @PathVariable String oldPassword) {
        String status;
        try{
            if (newUser.getPassword().equals(oldPassword)) {
                usersDAO.update(newUser);
                status= "successfully";
            }else status= "wrong password";
        }catch (Exception e){
            status = e.getMessage();
        }
        return status;
    }


}
