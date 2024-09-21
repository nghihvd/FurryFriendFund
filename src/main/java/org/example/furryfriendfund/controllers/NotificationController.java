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
    @Autowired
    private NotificationRepository notificationRepository;

    //http://localhost:8081/notification/15238822/status?status=true
    @PutMapping("/{notiID}/status")
    public ResponseEntity<?> updateRegisStatus(@PathVariable String notiID,
                                               @RequestParam boolean status) {
        try{
            notificationService.updateAccountStatusNotification(notiID,status);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }

    }


}
