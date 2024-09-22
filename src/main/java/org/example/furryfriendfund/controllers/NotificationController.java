package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationRepository;
import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    //http://localhost:8081/notification/15238822/status?status=true
    @PutMapping("/{notiID}/status")
    public ResponseEntity<?> updateRegisStatus(@PathVariable String notiID,
                                               @RequestParam boolean status) {
        boolean result = notificationService.updateAccountStatusNotification(notiID,status);
            if(result){
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body("Nothing changed");

    }


}
