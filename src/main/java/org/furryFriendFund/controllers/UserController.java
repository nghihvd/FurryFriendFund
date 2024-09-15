package org.furryFriendFund.controllers;

import org.furryFriendFund.user.UsersDTO;
import org.furryFriendFund.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // Định nghĩa URL với biến userID
    @GetMapping("/users/{userID}")
    public UsersDTO getUserById(@PathVariable String userID) {
        // Lấy thông tin người dùng từ database dựa trên userID
        return userService.getUserById(userID);
    }
    @GetMapping("/login")
    public String login(String userID){
        if(userService.login(userID)){
            return "logged successfully";
        }else{
            return "logged failed";
        }
    }
}
