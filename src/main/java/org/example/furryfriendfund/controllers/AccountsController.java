package org.example.furryfriendfund.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.furryfriendfund.accounts.*;

//import org.example.furryfriendfund.config.PasswordEncoder;
import org.example.furryfriendfund.jwt.JwtTokenProvider;
import org.example.furryfriendfund.jwt.TokenBlackListService;
import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationService;
import org.example.furryfriendfund.payload.LoginRequest;
import org.example.furryfriendfund.payload.LoginResponse;
import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RestController
// đánh dấu class là 1 controller (xử lý yêu cầu HTTP từ client, kết hợp với @ResponseBody để trả về
// định dạng JSON)
@RequestMapping("/accounts")
public class AccountsController {
    @Autowired
    private IAccountsService accountsService;
    @Autowired
    private PetsService petsService;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private TokenBlackListService tokenBlackListService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @PreAuthorize("hasAuthority('2') or hasAuthority('1') or hasAuthority('3') " )
    public ResponseEntity<?> updateUser(@RequestBody AccountDTO newInfor, @PathVariable String oldPassword) {
        ResponseEntity<?> status;
        Accounts accounts = accountsService.getUserById(newInfor.getAccountID());
        try{
            if(accounts != null) {
                if (passwordEncoder.matches(oldPassword, accounts.getPassword())) {
                    if(newInfor.getPassword() != null && !newInfor.getPassword().trim().isEmpty()){
                        if(!passwordEncoder.matches(newInfor.getPassword(), accounts.getPassword())) {
                            accounts.setPassword(passwordEncoder.encode(newInfor.getPassword()));
                        }
                    }
                    accounts.setBirthdate(newInfor.getBirthdate());
                    accounts.setName(newInfor.getName());
                    accounts.setAddress(newInfor.getAddress());
                    accounts.setPhone(newInfor.getPhone());
                    accounts.setSex(newInfor.getSex());
                    Accounts  acc = accountsService.save(accounts);
                    status = ResponseEntity.ok(acc);
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
            SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
            String newBirth = date.format(accounts.getBirthdate());
            AccountResponse accRes = new AccountResponse(
                    accounts.getAccountID(),
                    accounts.getPassword(),
                    accounts.getName(),
                    accounts.getRoleID(),
                    accounts.getNote(),
                    accounts.getSex(),
                    newBirth,
                    accounts.getAddress(),
                    accounts.getPhone(),
                    accounts.getTotal_donation());
            status = ResponseEntity.ok(accRes);
        }else status = null;
        return status;
    }

    /**
     * để kiểm tra thông tin của người đăng kí
     * @param accounts : thực thể là mỗi tài khoản

     * @return về trang main / trang chủ
     */

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest accounts) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        accounts.getAccountID(),
                        accounts.getPassword()
                )
        );
        //if not exception means information is available
        //set athentication information into Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // return jwt to user
        String jwt = tokenProvider.generateToken((LoggerDetail) authentication.getPrincipal());
        return new LoginResponse(jwt);
    }

    /**
     * để xóa phiên làm việc của người đăng nhập
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // get token from header
        String token = authorizationHeader.substring(7);
        if(token != null && jwtTokenProvider.validateToken(token)) {
            // add token to black list
            tokenBlackListService.addTokenToBlackList(token);
            return ResponseEntity.ok("logged out successfully");
        } else{
            return ResponseEntity.badRequest().body("Invalid token");
        }

    }

    @GetMapping("/getByID")
    @PreAuthorize("hasAuthority('2') or hasAuthority('1') or hasAuthority('3') " )
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
    @PreAuthorize("hasAuthority('1')" )
    public ResponseEntity<?> banAccount(@RequestBody Notification notification) {
        ResponseEntity<BaseResponse> response;
        try {
            Notification noti = notificationService.findNoti(notification.getNotiID());
            Pets pet = petsService.findPetById(noti.getPetID());
            Accounts banAccount = accountsService.getUserById(pet.getAccountID());
            if(banAccount != null) {
                banAccount.setNote("Banned");
                accountsService.save(banAccount);
                notificationService.deleteNoti(noti.getNotiID());
                response = ResponseUtils.createSuccessRespone("Account Banned", banAccount);
            }else {
                response = ResponseUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/showDonators")
    public ResponseEntity<BaseResponse> showDonators() {
        ResponseEntity<BaseResponse> response;
        try {
            List<Accounts> donators = accountsService.showDonators();
            response = ResponseUtils.createSuccessRespone("Donators", donators);
        }catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;

    }

    @GetMapping("/getAllAccounts")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> getAllAccounts() {
        List<Accounts> accounts = accountsService.getAllAccounts();
        if(accounts != null) {
            return ResponseUtils.createSuccessRespone("Accounts", accounts);
        }
        return ResponseUtils.createErrorRespone("No accounts found", null, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{accountID}/{button}")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> changeAccountStatus(@PathVariable String accountID,@PathVariable String button) {
        Accounts accounts = accountsService.getUserById(accountID);
        if(accounts != null) {
            String mess = accountsService.ChangeStatus(accounts, button);
            return ResponseUtils.createSuccessRespone(mess, accounts);
        }
        return ResponseUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
    }



}
