package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.user.UserService;
import org.example.furryfriendfund.user.UsersDTO;
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
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UsersDTO> register(@RequestBody UsersDTO usersDTO) {
        UsersDTO users = userService.registerUser(usersDTO);
        return ResponseEntity.created(URI.create("/user/register")).body(users);
    }
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
