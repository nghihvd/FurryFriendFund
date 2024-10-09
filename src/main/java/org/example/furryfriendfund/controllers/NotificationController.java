package org.example.furryfriendfund.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.http.ResponseUtil;
import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationRepository;
import org.example.furryfriendfund.notification.NotificationService;
import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PetsService petsService;
    @Autowired
    private AccountsService accountsService;

    //http://localhost:8081/notification/15238822/status?status=true

    /**
     * update status of notication including register, add new pet
     * @param notiID notification id
     * @param status if admin click "Accept" -> status: true, if admin click "Deny" -> status: false
     * @return http status + message
     */
    @PutMapping("/{notiID}/status")
    public ResponseEntity<?> updateRegisStatus(@PathVariable String notiID,
                                               @RequestParam boolean status) {

        Notification find = notificationService.findNoti(notiID);

        if(find.getPetID() == null) {
            boolean result = notificationService.updateAccountStatusNotification(notiID,status);

            if(result){
                return ResponseEntity.ok().build();
            }
        }

        if(find.getPetID() != null){
            boolean result = notificationService.updatePetsStatusNotification(notiID,status);

            if(result){
                notificationService.acceptNewPetNoti(find.getPetID(), find.getMessage().split("_")[1].split(" ")[0]);
                return ResponseEntity.ok().build();
            } else{
                notificationService.denyNewPetNoti(find.getPetID(), find.getMessage().split("_")[1].split(" ")[0]);
                notificationService.deleteNoti(notiID);
                petsService.deletePet(find.getPetID());
                return ResponseEntity.ok().body("Delete pet and notification");
            }
        }

        return ResponseEntity.ok().body("Nothing changed");
    }

    /**
     * Hiển thị các thông báo về việc nận thêm pet mới vào trại
     * @return list các pet waiting for accept to be stay in the shelter
     */
    @GetMapping("/showAdminAdoptNoti")
    public ResponseEntity<?> showNoti() {
        List<Notification> list =  notificationService.showNotifications(1);
        if(list.isEmpty()){
            return ResponseEntity.badRequest().body("No notifications found");
        }
        List<Notification> acceptAdopt = new ArrayList<>();
        for(Notification n : list){
            if(n.getPetID() != null && n.isButton_status()&&n.getMessage().contains("can be added to our shelter??")){
                acceptAdopt.add(n);
            }
        }
        if(acceptAdopt.isEmpty()){
            return ResponseEntity.badRequest().body("No adopt notifications found");
        }
        return ResponseEntity.ok().body(acceptAdopt);
    }

    /**
     * show notification of staff includes accept noti from admin
     * @param request to get session for know who is using that account
     * @return
     */
    @GetMapping("/showStaffNoti")
    public ResponseEntity<BaseResponse> showStaffNoti(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null){
            return ResponseUtils.createErrorRespone("Session expired", null, HttpStatus.NOT_FOUND);
        }
        Accounts acc = (Accounts) session.getAttribute("accountID");
        if(acc == null){
            return ResponseUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
        }
        List<Notification> list =  notificationService.showNotifications(2);
        List<Notification> accountNoti  = notificationService.showNotificationsAccountID(acc.getAccountID());

        Set<Notification> setNoti = new HashSet<>(list);
        setNoti.addAll(accountNoti);
    return ResponseUtils.createSuccessRespone("", setNoti);
    }

    /**
     * show member notification which have noti from staff
     * @param request to get session
     * @return
     */
    @GetMapping("/memberNoti")
    public ResponseEntity<BaseResponse> showMemberNoti(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null){
            return ResponseUtils.createErrorRespone("Session expired", null, HttpStatus.NOT_FOUND);
        }
        Accounts acc = (Accounts) session.getAttribute("accountID");
        if(acc == null){
            return ResponseUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
        }
        List<Notification> list =  notificationService.showNotifications(3);
        List<Notification> accountNoti  = notificationService.showNotificationsAccountID(acc.getAccountID());

        Set<Notification> setNoti = new HashSet<>(list);
        setNoti.addAll(accountNoti);
        return ResponseUtils.createSuccessRespone("", setNoti);
    }


    /**
     * show text notification of admin
     * @param request to get session account
     * @return list of notification
     */
    @GetMapping("/otherAdminNoti")
    public ResponseEntity<BaseResponse> otherNoti(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if(session == null){
//            return ResponseUtils.createErrorRespone("Session expired", null, HttpStatus.NOT_FOUND);
//        }
//        System.out.println("aaaa");
//        Accounts acc = (Accounts) session.getAttribute("accountID");
//        if(acc == null){
//            return ResponseUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
//
//        }
//        System.out.println("aaaa");
        List<Notification> list =  notificationService.showNotifications(1);
        List<Notification> other = new ArrayList<>();
        for(Notification n : list){
            if(!n.isButton_status()){
                other.add(n);
            }
        }
        if(other.isEmpty()){
            return ResponseUtils.createErrorRespone("No notifications found", null, HttpStatus.NOT_FOUND);
        }
        return ResponseUtils.createSuccessRespone("", other);
    }

    @GetMapping("/showRegisNoti")
    public ResponseEntity<BaseResponse> showRegisNoti(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if(session == null){
//            return ResponseUtils.createErrorRespone("Session expired", null, HttpStatus.NOT_FOUND);
//        }
//        Accounts acc = (Accounts) session.getAttribute("accountID");
//        if(acc == null){
//            return ResponseUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
//
//        }
        List<Notification> newList = notificationService.showRegisNoti();
        if(newList.isEmpty()){
            return ResponseUtils.createErrorRespone("No notifications found", null, HttpStatus.BAD_REQUEST);
        }
        return ResponseUtils.createSuccessRespone("", newList);
    }

    @PostMapping("/remindReport")
    public ResponseEntity<BaseResponse> remindReport(@RequestBody Pets pet) {
        ResponseEntity<BaseResponse> response;
        try{
            Pets pets = petsService.findPetById(pet.getPetID());
            if(pets != null){
                Accounts accounts = accountsService.getUserById(pets.getAccountID());
                Notification notification = notificationService.remindReportNotification(pets);
                notificationService.save(notification);
            response = ResponseUtils.createSuccessRespone("The notification will be sent to "+accounts.getName(), null);
            }else{
                response = ResponseUtils.createErrorRespone("Pet not found", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PostMapping("/banRequest/{staffID}")
    public ResponseEntity<BaseResponse> banRequest(@RequestBody Pets pet, @PathVariable String staffID) {
        ResponseEntity<BaseResponse> response;
        try {

            Pets pets = petsService.findPetById(pet.getPetID());
            Accounts staff = accountsService.getUserById(staffID);
            if(pets != null){
                Notification notification = notificationService.banRequestNotification(pets, staff);
                notificationService.save(notification);
                response = ResponseUtils.createSuccessRespone("Request will be sent to admin", null);
            }else{
                response = ResponseUtils.createErrorRespone("Pet not found", null, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/showBanRequest")
    public ResponseEntity<BaseResponse> showBanRequest() {
        ResponseEntity<BaseResponse> response;
        List<Notification> list = notificationService.getBanRequestNotifications();
        if(list.isEmpty()){
            response = ResponseUtils.createErrorRespone("No notifications found", null, HttpStatus.NOT_FOUND);
        }else {
            response = ResponseUtils.createSuccessRespone("", list);
        }
        return response;
    }

    @DeleteMapping("/deleteNotification")
    public ResponseEntity<BaseResponse> deleteNotification(@RequestBody Notification notification) {
        ResponseEntity<BaseResponse> response;
        try {
            Notification noti = notificationService.findNoti(notification.getNotiID());
            notificationService.deleteNoti(noti.getNotiID());
            response = ResponseUtils.createSuccessRespone("Notification deleted", null);
        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;

    }

}
