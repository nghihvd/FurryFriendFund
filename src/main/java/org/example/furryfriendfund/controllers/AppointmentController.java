package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.appointments.AppointmentsService;
import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    AppointmentsService appointmentsService;

    /**
     * Gửi yêu cầu nhận nuôi thú cưng
     * @param appointments
     * @return
     */
    @PostMapping("/adopt")
    public ResponseEntity<?> adopt(@RequestBody Appointments appointments) {
        ResponseEntity<?> status;

        try {
            //lấy thông tin thú cưng và account và gửi thông báo cho staff
            String accountID = appointments.getAccountID();
            String petID = appointments.getPetID();
            //tạo thông báo
            notificationService.adoptNotification(accountID, petID);

            appointmentsService.save(appointments);
            status = ResponseEntity.ok("Send request successfully, please waiting for staff response");
        }catch (Exception e){
            status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return status;
    }

    @DeleteMapping("/refuse/{reason}")
    public ResponseEntity<?> refuse(@RequestBody Appointments appointments, @PathVariable String reason) {
        ResponseEntity<?> status;
        try {
            String accountID = appointments.getAccountID();
            appointmentsService.delete(appointments);
            notificationService.refuseAdoptRequestNotification(appointments, reason);

            status = ResponseEntity.ok("You have refused the appointment, reason will send to member.");
        } catch (Exception e) {
            status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return status;
    }
}
