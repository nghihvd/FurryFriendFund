package org.furryFriendFund.controllers;

import org.furryFriendFund.user.IUsersService;
import org.furryFriendFund.user.UsersService;
import org.furryFriendFund.user.UsersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UsersService usersDAO;

    //oldPassword kiểm tra thông tin mật khẩu người dùng có nhập nếu đúng thì mới cho nhập
    @PutMapping("/update/{oldPassword}")
    public String updateUser(@RequestBody UsersDTO newUser, @PathVariable String oldPassword) {
        String status;
        try{
            if (newUser.getPassword().equals(oldPassword)) {
                usersDAO.update(newUser);
                status= "success";
            }else status= "wrong password";
        }catch (Exception e){
            status = e.getMessage();
        }
        return status;
    }


}
