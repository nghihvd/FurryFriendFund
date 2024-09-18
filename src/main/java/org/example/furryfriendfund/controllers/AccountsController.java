package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.accounts.Accounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
// đánh dấu class là 1 controller (xử lý yêu cầu HTTP từ client, kết hợp với @ResponseBody để trả về
// định dạng JSON)
@RequestMapping("/accounts")
public class AccountsController {
    @Autowired
    private AccountsService accountsService;

    @PostMapping("/register")
    public ResponseEntity<Accounts> register(@RequestBody Accounts accountsDTO) {
        Accounts accounts = accountsService.saveUser(accountsDTO);
        return ResponseEntity.created(URI.create("/accounts/register")).body(accounts);
    }
    //oldPassword kiểm tra thông tin mật khẩu người dùng có nhập nếu đúng thì mới cho nhập
    @PutMapping("/update/{oldPassword}")
    public String updateUser(@RequestBody Accounts newUser, @PathVariable String oldPassword) {
        String status;
        try{
            if (newUser.getPassword().equals(oldPassword)) {
                accountsService.update(newUser);
                status= "successfully";
            }else status= "wrong password";
        }catch (Exception e){
            status = e.getMessage();
        }
        return status;
    }
    // Định nghĩa URL với biến userID
    @GetMapping("/accounts/{accountsID}")
    public Accounts getUserById(@PathVariable String accountsID) {
        // Lấy thông tin người dùng từ database dựa trên userID
        return accountsService.getUserById(accountsID);
    }
    @PostMapping("/login")
    public String login(@RequestParam String accountID, @RequestParam String password) {
        if(accountsService.ckLogin(accountID, password)) {
            return "logged successfully";
        } else {
            return "login failed";
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        accountsService.logout();
        return ResponseEntity.ok("logged out successfully");
    }


}
