package org.example.furryfriendfund.controllers;

import io.swagger.annotations.Authorization;
import jakarta.mail.MessagingException;
import org.example.furryfriendfund.OTP.EmailService;
import org.example.furryfriendfund.OTP.OTPService;
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

import java.util.List;

@RestController
// đánh dấu class là 1 controller (xử lý yêu cầu HTTP từ client, kết hợp với
// @ResponseBody để trả về
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPService otpService;
    /**
     * hàm đăng kí tài khoản mới
     * 
     * @param accountsDTO biến chứa các thông tin ng dùng nhập
     * @return toàn bộ thông tin nếu thành cng còn nếu không thì hiện yêu cầu nhập
     *         accountID mới
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Accounts accountsDTO) {
        try {
            String opt = otpService.generateOTP(accountsDTO.getEmail());
            emailService.sendSimpleEmail(accountsDTO.getEmail(),
                    "Verify your Furry Friend Fund account",
                    opt);
            accountsDTO.setExperience_caring(false);

            accountsDTO.setCitizen_serial(null);
            accountsDTO.setJob(null);
            accountsDTO.setConfirm_address(null);
            accountsDTO.setIncome(0);
            Accounts accounts = accountsService.saveAccountsInfo(accountsDTO);
            return ResponseEntity.created(URI.create("/accounts/register"))
                    .body(accounts.getName() + " register success");
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest().body("Please enter another accountID");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("cannot send to your phone number please check again");
        }
    }

    @PostMapping("/{accID}/verifyOTP")
    public ResponseEntity<BaseResponse> verifyOTP(@RequestParam String email, @RequestParam String otp,@PathVariable String accID) {
        if(otpService.validateOTP(otp,email)) {
            boolean re = accountsService.changeStatusAcc(accID);
            if(re) {
                return ResponseUtils.createSuccessRespone("OTP verify successfull", null);
            }
        }
            return ResponseUtils.createErrorRespone("Invalid OTP or your OTP is expried",null,HttpStatus.NOT_FOUND);

    }
    @GetMapping("/resendOTP/{accountID}")
    public ResponseEntity<BaseResponse> resendOTP(@PathVariable String accountID) throws MessagingException {
        Accounts accountsDTO = accountsService.getUserById(accountID);
        String opt = otpService.generateOTP(accountsDTO.getEmail());
        emailService.sendSimpleEmail(accountsDTO.getEmail(),"Verify your Furry Friend Fund account",opt);
        return  ResponseUtils.createSuccessRespone("OTP resend success", null);
    }
    /**
     * update thông tin xác thực của adopter
     * @param accountID acc adopter
     * @param married tình tranạng hôn nhn
     * @param job cong việc
     * @param income thu  nhâp
     * @param citizen_serial CCCD
     * @param experience_caring kinh nghiem cham soc
     * @param confirm_address dia chi cònfirm
     * @return
     */
    @PutMapping("confirmationInfor/{accountID}")
    @Authorization("hasAuthority('2')")
    public ResponseEntity<BaseResponse> ConfirmationAdopter(@PathVariable String accountID,
                                                            @RequestParam boolean married,
                                                            @RequestParam String job,
                                                            @RequestParam int income,
                                                            @RequestParam String citizen_serial,
                                                            @RequestParam boolean experience_caring,
                                                            @RequestParam String confirm_address) {
        ResponseEntity<BaseResponse> response = null;
        boolean result = accountsService.Verify( accountID, married, job, income, citizen_serial, experience_caring, confirm_address);
        if(result){
            response = ResponseUtils.createSuccessRespone("Account verified", null);

        } else{
            response = ResponseUtils.createErrorRespone("Account not verified",null,HttpStatus.NOT_FOUND);
        }
        return  response;
    }

    /**
     * lấy thông tin xác thực của adopter
     * @param accountID acc adopter
     * @return thong tin xaác tực
     */
    @GetMapping("getConfirm/{accountID}")
    public ResponseEntity<BaseResponse> GetConfirmation(@PathVariable String accountID){
        ResponseEntity<BaseResponse> response = null;
        Accounts acc = accountsService.getUserById(accountID);
        if(acc != null){
            response = ResponseUtils.createSuccessRespone("", acc);
        } else{
            response = ResponseUtils.createErrorRespone("No information",null,HttpStatus.NO_CONTENT);
        }
        return response;
    }
    /**
     * Cập nhật thông tin tài khoản của người dùng
     * 
     * @param newInfor    thông tin mới của tài khoản mà người dùng đã nhập
     * @param oldPassword mật khẩu của tài khoản trc khi thục hiện cập nhật
     * @return trả về status là một ResponseEntity<?> linh hoạt thay đổi kiểu dữ
     *         liệu khi gặp lỗi
     *
     */
    @PutMapping("/update/{oldPassword}")
    @PreAuthorize("hasAuthority('2') or hasAuthority('1') or hasAuthority('3') ")
    public ResponseEntity<?> updateUser(@RequestBody AccountDTO newInfor, @PathVariable String oldPassword) {
        ResponseEntity<?> status;
        Accounts accounts = accountsService.getUserById(newInfor.getAccountID());
        try {
            if (accounts != null) {
                if (passwordEncoder.matches(oldPassword, accounts.getPassword())) {
                    if (newInfor.getPassword() != null && !newInfor.getPassword().trim().isEmpty()) {
                        if (!passwordEncoder.matches(newInfor.getPassword(), accounts.getPassword())) {
                            accounts.setPassword(passwordEncoder.encode(newInfor.getPassword()));
                        }
                    }
                    accounts.setBirthdate(newInfor.getBirthdate());
                    accounts.setName(newInfor.getName());
                    accounts.setAddress(newInfor.getAddress());
                    accounts.setPhone(newInfor.getPhone());
                    accounts.setSex(newInfor.getSex());
                    Accounts acc = accountsService.save(accounts);
                    status = ResponseEntity.ok(acc);
                } else
                    status = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong password");
            } else
                status = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        } catch (Exception e) {
            status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return status;
    }

    /**
     *
     * @param accountsID là username hay tên đăng nhập của người đăng nhập
     * @return về tất cả thông tin của account có accountID trùng với accountID được
     *         nhập vào
     */
    @GetMapping("/search/{accountsID}")
    public ResponseEntity<?> getUserById(@PathVariable String accountsID) {
        // Lấy thông tin người dùng từ database dựa trên userID
        ResponseEntity<?> status;
        Accounts accounts = accountsService.getUserById(accountsID);

        if (accounts != null) {
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
        } else
            status = null;
        return status;
    }

    /**
     * để kiểm tra thông tin của người đăng kí
     * 
     * @param accounts : thực thể là mỗi tài khoản
     * 
     * @return về trang main / trang chủ
     */

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest accounts) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            accounts.getAccountID(),
                            accounts.getPassword()));

            LoggerDetail userDetails = (LoggerDetail) authentication.getPrincipal();

            // Kiểm tra xem tài khoản có note là "Available" không
            if (!userDetails.isAccountNonLocked()) {
                return new LoginResponse("Account is not available");
            }
            // if not exception means information is available
            // set athentication information into Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // return jwt to user
            String jwt = tokenProvider.generateToken((LoggerDetail) authentication.getPrincipal());
            return new LoginResponse(jwt);
        } catch (Exception e) {
            return new LoginResponse("Account is not available");
        }
    }

    /**
     * để xóa phiên làm việc của người đăng nhập
     * 
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // get token from header
        String token = authorizationHeader.substring(7);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // add token to black list
            tokenBlackListService.addTokenToBlackList(token);
            return ResponseEntity.ok("logged out successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }

    }

    @GetMapping("/getByID")
    @PreAuthorize("hasAuthority('2') or hasAuthority('1') or hasAuthority('3') ")
    public ResponseEntity<BaseResponse> getByID(@RequestBody Accounts account) {
        ResponseEntity<BaseResponse> response;

        Accounts acc = accountsService.getUserById(account.getAccountID());
        if (acc != null) {
            response = ResponseUtils.createSuccessRespone("Account found", acc);

        } else {
            response = ResponseUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @PutMapping("/banAccount")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<?> banAccount(@RequestBody Notification notification) {
        ResponseEntity<BaseResponse> response;
        try {
            Notification noti = notificationService.findNoti(notification.getNotiID());
            Pets pet = petsService.findPetById(noti.getPetID());
            Accounts banAccount = accountsService.getUserById(pet.getAccountID());
            if (banAccount != null) {
                banAccount.setNote("Banned");
                accountsService.save(banAccount);
                notificationService.deleteNoti(noti.getNotiID());
                response = ResponseUtils.createSuccessRespone("Account Banned", banAccount);
            } else {
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
        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;

    }



    @GetMapping("/searchByNameAndAccountID")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> searchByNameAndAccountID(
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "role", required = false, defaultValue = "0") int role,
            @RequestParam(value = "id", required = false, defaultValue = "") String accountID,
            @RequestParam(value = "note", required = false, defaultValue = "") String note ) {

        List<Accounts> foundAcc;

        try {
            foundAcc = accountsService.searchAccByName(name, role, accountID,note);
            if (foundAcc.isEmpty()) {
                return ResponseUtils.createErrorRespone("Account not found", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseUtils.createErrorRespone("Account not found", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseUtils.createSuccessRespone("Account found", foundAcc);
    }

    @GetMapping("/getAllAccounts")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> getAllAccounts() {
        List<Accounts> accounts = accountsService.getAllAccounts();
        if (accounts != null) {
            return ResponseUtils.createSuccessRespone("Accounts", accounts);
        }
        return ResponseUtils.createErrorRespone("No accounts found", null, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{accountID}/{button}")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> changeAccountStatus(@PathVariable String accountID,
            @PathVariable String button) {
        Accounts accounts = accountsService.getUserById(accountID);
        if (accounts != null) {
            String mess = accountsService.ChangeStatus(accounts, button);
            return ResponseUtils.createSuccessRespone(mess, accounts);
        }
        return ResponseUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/checkAcocunt")
    public ResponseEntity<BaseResponse> checkAccount(@RequestHeader("Authorization") String token) {
        ResponseEntity<BaseResponse> response;
        String accountID = jwtTokenProvider.getAccountIDFromJWT(token);
        int roleID = jwtTokenProvider.getRolesFromJWT(token);
        Accounts accounts = accountsService.getUserById(accountID);
        if (accounts.getNote().equals("Banned") || accounts.getRoleID() != roleID) {
            response = ResponseUtils.createErrorRespone("Account is changed some thing", null, HttpStatus.FORBIDDEN);
        } else {
            response = ResponseUtils.createSuccessRespone("Account found", accounts);
        }
        return response;

    }

}
