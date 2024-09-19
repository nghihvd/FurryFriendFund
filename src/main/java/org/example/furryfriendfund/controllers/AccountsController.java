package org.example.furryfriendfund.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.accounts.Accounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
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
     *
     * @param accountsDTO
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Accounts accountsDTO) {
        try {
            Accounts accounts = accountsService.saveAccountsInfo(accountsDTO);
            return ResponseEntity.created(URI.create("/accounts/register")).body(accounts);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Please enter another accountID");
        }
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

    /**
     * để check xem đăng kí thành công không?
     * @param accountID
     * @param password
     * @return ra trang main/ trang chủ
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String accountID, @RequestParam String password, HttpServletRequest request) {

        ResponseEntity<String> status;

        //lấy thông tin của người đăng nhập
        accountID = request.getParameter("accountID");
        password = request.getParameter("password");


        HttpSession session = request.getSession();
        session.setAttribute("accountID", accountID);
        session.setAttribute("password", password);

        if(accountsService.ckLogin(accountID, password)) {
            //HttpSession session = request.getSession();
            Cookie cookie = new Cookie("accountID",accountID);
            cookie.setMaxAge(60*60); // cookies sẽ hết trong vòng 1 tiếng
            cookie.setSecure(true);//cookie chỉ được gửi qua HTTPS
            cookie.setHttpOnly(true);//ngăn chặn JS truy cập trực tiếp vào content cookies

          //  .addCookie(cookie);//add cookies vô response để gửi phản hồi đến client
             status = ResponseEntity.ok("logged successfully");

        } else {
            status= ResponseEntity.ok("login failed");
        }
        return status;
    }

    /**
     * để xóa phiên làm việc của người đăng nhập
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {

            HttpSession session = request.getSession(false);
            if (session != null) {

                Cookie[] cookies = request.getCookies();
                for (Cookie x : cookies) {
                    x.setMaxAge(0);
                    response.addCookie(x);
                }
                session.invalidate();
           }
        return ResponseEntity.ok("logged out successfully");
    }


}
