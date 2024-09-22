package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.appointments.AppointmentsService;
import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
