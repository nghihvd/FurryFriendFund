package org.example.furryfriendfund.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.http.ResponseUtil;
import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationRepository;
import org.example.furryfriendfund.notification.NotificationService;
import org.example.furryfriendfund.respone.BaseRespone;
import org.example.furryfriendfund.respone.ResponeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    //http://localhost:8081/notification/15238822/status?status=true
    @PutMapping("/{notiID}/status")
    public ResponseEntity<?> updateRegisStatus(@PathVariable String notiID,
                                               @RequestParam boolean status) {

        Notification find = notificationService.findNoti(notiID);
        if(find.getPetID().isEmpty()){
            boolean result = notificationService.updateAccountStatusNotification(notiID,status);
            if(result){
                return ResponseEntity.ok().build();
            }
        }
        if(!find.getPetID().isEmpty()){
            boolean result = notificationService.updatePetsStatusNotification(notiID,status);
            if(result){
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.badRequest().body("Nothing changed");
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
        List<Notification> acceptAdopt = null;
        for(Notification n : list){
            if(n.getPetID() != null && n.isButton_status()){
                acceptAdopt.add(n);
            }
        }
        return ResponseEntity.ok().body(acceptAdopt);
    }

    /**
     * show notification of staff includes accept noti from admin
     * @param request to get session for know who is using that account
     * @return
     */
    @GetMapping("/showStaffNoti")
    public ResponseEntity<BaseRespone> showStaffNoti(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null){
            return ResponeUtils.createErrorRespone("Session expired", null, HttpStatus.NOT_FOUND);
        }
        Accounts acc = (Accounts) session.getAttribute("accountID");
        if(acc == null){
            return ResponeUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
        }
        List<Notification> list =  notificationService.showNotifications(2);
        List<Notification> accountNoti  = notificationService.showNotificationsAccountID(acc.getAccountID());

        Set<Notification> setNoti = new HashSet<>(list);
        setNoti.addAll(accountNoti);
    return ResponeUtils.createSuccessRespone("", setNoti);
    }

    /**
     * show member notification which have noti from staff
     * @param request to get session
     * @return
     */
    @GetMapping("/memberNoti")
    public ResponseEntity<BaseRespone> showMemberNoti(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null){
            return ResponeUtils.createErrorRespone("Session expired", null, HttpStatus.NOT_FOUND);
        }
        Accounts acc = (Accounts) session.getAttribute("accountID");
        if(acc == null){
            return ResponeUtils.createErrorRespone("No account found", null, HttpStatus.NOT_FOUND);
        }
        List<Notification> list =  notificationService.showNotifications(3);
        List<Notification> accountNoti  = notificationService.showNotificationsAccountID(acc.getAccountID());

        Set<Notification> setNoti = new HashSet<>(list);
        setNoti.addAll(accountNoti);
        return ResponeUtils.createSuccessRespone("", setNoti);
    }

}
