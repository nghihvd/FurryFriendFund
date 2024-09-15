package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.user.UserService;
import org.example.furryfriendfund.user.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
// đánh dấu class là 1 controller (xử lý yêu cầu HTTP từ client, kết hợp với @ResponseBody để trả về
// định dạng JSON)
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService usersDAO;
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users usersDTO) {
        Users users = userService.registerUser(usersDTO);
        return ResponseEntity.created(URI.create("/user/register")).body(users);
    }
    //oldPassword kiểm tra thông tin mật khẩu người dùng có nhập nếu đúng thì mới cho nhập
    @PutMapping("/update/{oldPassword}")
    public String updateUser(@RequestBody Users newUser, @PathVariable String oldPassword) {
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
    // Định nghĩa URL với biến userID
    @GetMapping("/users/{userID}")
    public Users getUserById(@PathVariable String userID) {
        // Lấy thông tin người dùng từ database dựa trên userID
        return userService.getUserById(userID);
    }
    @PostMapping("/login")
    public String login(@RequestParam String userID, @RequestParam String password) {
        if(userService.ckLogin(userID, password)) {
            return "logged successfully";
        } else {
            return "login failed";
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        userService.logout();
        return ResponseEntity.ok("logged out successfully");
    }


}
