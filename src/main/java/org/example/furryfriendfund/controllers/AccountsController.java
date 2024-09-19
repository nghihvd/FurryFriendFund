package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.accounts.Accounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
// đánh dấu class là 1 controller (xử lý yêu cầu HTTP từ client, kết hợp với @ResponseBody để trả về
// định dạng JSON)
@RequestMapping("/accounts")
public class AccountsController {
    @Autowired
    private AccountsService accountsService;

    /**
     * hàm này dùng đẻ lưu thông tin mà ng dùng nập
     * @param model là biến dùng để
     * @return trang đăng kí
     */
    @GetMapping("/regisPage")
    public String showRegisterForm(Model model){
        model.addAttribute("account", new Accounts());
        return "regisPage";
    }
    @PostMapping("/register")
    public ResponseEntity<Accounts> register(@RequestBody Accounts accountsDTO) {
        Accounts accounts = accountsService.saveAccountsInfo(accountsDTO);
        return ResponseEntity.created(URI.create("/accounts/register")).body(accounts);
    }
    //oldPassword kiểm tra thông tin mật khẩu người dùng có nhập nếu đúng thì mới cho nhập
    @PutMapping("/update/{oldPassword}")
    public ResponseEntity<?> updateUser(@RequestBody Accounts newInfor, @PathVariable String oldPassword) {
        ResponseEntity<?> status;
        Accounts accounts = accountsService.getUserById(newInfor.getAccountID());
        try{
            if(accounts != null) {
                if (accounts.getPassword().equals(oldPassword)) {
                    accountsService.saveAccountsInfo(newInfor);
                    status = ResponseEntity.ok(newInfor);
                } else status = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong password");
            }else status = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }catch (Exception e){
            status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
