package org.example.furryfriendfund.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.accounts.Accounts;

import org.example.furryfriendfund.accounts.IAccountsService;
import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.appointments.AppointmentsService;
import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationService;
import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
// đánh dấu class là 1 controller (xử lý yêu cầu HTTP từ client, kết hợp với @ResponseBody để trả về
// định dạng JSON)
@RequestMapping("/accounts")
public class AccountsController {
    @Autowired
    private IAccountsService accountsService;
    @Autowired
    private PetsService petsService;


    /**
     * hàm đăng kí tài khoản mới
     * @param accountsDTO biến chứa các thông tin ng dùng nhập
     * @return toàn bộ thông tin nếu thành cng còn nếu không thì hiện yêu cầu nhập accountID mới
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Accounts accountsDTO) {
        try {
            Accounts accounts = accountsService.saveAccountsInfo(accountsDTO);
            return ResponseEntity.created(URI.create("/accounts/register")).body(accounts.getName()+" register success");
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Please enter another accountID");
        }
    }

    /**
     * Cập nhật thông tin tài khoản của người dùng
     * @param newInfor thông tin mới của tài khoản mà người dùng đã nhập
     * @param oldPassword mật khẩu của tài khoản trc khi thục hiện cập nhật
     * @return trả về status là  một ResponseEntity<?> linh hoạt thay đổi kiểu dữ liệu khi gặp lỗi
     *
     */
    @PutMapping("/update/{oldPassword}")
    public ResponseEntity<?> updateUser(@RequestBody Accounts newInfor, @PathVariable String oldPassword) {
        ResponseEntity<?> status;
        Accounts accounts = accountsService.getUserById(newInfor.getAccountID());
        try{
            if(accounts != null) {
                if (accounts.getPassword().equals(oldPassword)) {
                    accountsService.save(newInfor);
                    status = ResponseEntity.ok(newInfor);
                } else status = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong password");
            }else status = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }catch (Exception e){
            status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return status;
    }

    /**
     *
     * @param accountsID là username hay tên đăng nhập của người đăng nhập
     * @return về tất cả thông tin của account có accountID trùng với accountID được nhập vào
     */
    @GetMapping("/search/{accountsID}")
    public ResponseEntity<?> getUserById(@PathVariable String accountsID) {
        // Lấy thông tin người dùng từ database dựa trên userID
        ResponseEntity<?> status;
        Accounts accounts =accountsService.getUserById(accountsID);
        if(accounts != null) {
            status = ResponseEntity.ok(accounts);
        }else status = null;
        return status;
    }

    /**
     * để kiểm tra thông tin của người đăng kí
     * @param accounts : thực thể là mỗi tài khoản
     * @param request : yêu cầu của người đăng nhập
     * @param response : khi người đăng nhập nhấn nút đăng nhập (login)
     * @return về trang main / trang chủ
     */

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Accounts accounts, HttpServletRequest request, HttpServletResponse response) {

        String accountID = accounts.getAccountID();
        String password = accounts.getPassword();

        if (accountsService.ckLogin(accountID, password) && accountsService.getUserById(accountID).getNote().equals("Available")) {
            HttpSession session = request.getSession();
            session.setAttribute("accountID", accountsService.getUserById(accountID));

            Cookie cookie = new Cookie("accountID", accountID);
            cookie.setMaxAge(60 * 60); // Cookie expires in 1 hour
            response.addCookie(cookie);

            return ResponseEntity.ok(accountsService.getUserById(accountID));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
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

    @GetMapping("/getByID")
    public ResponseEntity<BaseResponse> getByID(@RequestBody Accounts account) {
        ResponseEntity<BaseResponse> response;

        Accounts acc = accountsService.getUserById(account.getAccountID());
        if(acc != null) {
            response = ResponseUtils.createSuccessRespone("Account found", acc);
        }else{
            response = ResponseUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @PutMapping("/banAccount")
    public ResponseEntity<?> banAccount(@RequestBody Pets pets) {
        ResponseEntity<BaseResponse> response;
        try {
            Pets pet = petsService.findPetById(pets.getPetID());
            Accounts banAccount = accountsService.getUserById(pet.getAccountID());
            if(banAccount != null) {
                banAccount.setNote("Banned");
                accountsService.save(banAccount);
                response = ResponseUtils.createSuccessRespone("Account Banned", banAccount);
            }else {
                response = ResponseUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }







}
