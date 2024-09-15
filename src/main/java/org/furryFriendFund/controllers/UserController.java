package org.furryFriendFund.controllers;

import org.furryFriendFund.user.UserRepository;
import org.furryFriendFund.user.UserService;
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
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UsersDTO> register(@RequestBody UsersDTO usersDTO) {
        UsersDTO regis = userService.registerUser(usersDTO);

        return ResponseEntity.created(URI.create("user/register")).body(regis);
    }
}
