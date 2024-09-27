package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.appointments.AppointmentsService;
import org.example.furryfriendfund.notification.NotificationService;
import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    AppointmentsService appointmentsService;

    @Autowired
    PetsService petsService;

    /**
     * Gửi yêu cầu nhận nuôi thú cưng
     * @param appointments
     * @return
     */
    @PostMapping("/adopt")
    public ResponseEntity<?> adopt(@RequestBody Appointments appointments) {
        ResponseEntity<?> status;

        try {
            String appointID = UUID.randomUUID().toString().substring(0, 8);
            appointments.setAppointID(appointID);
            //lấy thông tin thú cưng và account và gửi thông báo cho staff
            Pets pets = petsService.findPetById(appointments.getPetID());
            String accountID = appointments.getAccountID();
            String petID = appointments.getPetID();
            List<Appointments> checkApointInProgress = appointmentsService.findByAccountIDAndStatus(accountID,false);
            /*kiểm tra xem có yêu cầu nhận  nuôi nào của người dùng này chưa được sử lý ko
              nếu có thì thông báo lại và yêu cầu đợi yêu cầu trc đó đc giải quyết xong đã
            */
            if(pets.getStatus().equals("Available")) {
                if (checkApointInProgress.isEmpty()) {
                    //chỉnh status của pet
                    pets.setStatus("Waiting");
                    petsService.addPet(pets);

                    //tạo thông báo
                    notificationService.adoptNotification(accountID, petID);

                    appointmentsService.save(appointments);
                    status = ResponseEntity.ok("Send request successfully, please waiting for staff response");

                } else {
                    status = ResponseEntity.status(HttpStatus.CONFLICT).body("You have a request in progress, please wait until that request processed");
                }
            }else status = ResponseEntity.status(HttpStatus.CONFLICT).body("This pet are in progress adopt, please waiting for result or choose another one");

        }catch (Exception e){
            status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return status;
    }

    /**
     * Từ chối yêu cầu nhận nuôi
     * @param appointments
     * @param reason
     * @return
     */
    @DeleteMapping("/refuse/{reason}")
    public ResponseEntity<?> refuse(@RequestBody Appointments appointments, @PathVariable String reason) {
        ResponseEntity<?> status;
        try {
            Appointments appoint = appointmentsService.findById(appointments.getAppointID());
            //trả status về lại ban đầu
            Pets pets = petsService.findPetById(appoint.getPetID());
            pets.setStatus("Available");
            petsService.addPet(pets);



            appointmentsService.delete(appoint);
            notificationService.refuseAdoptRequestNotification(appoint, reason);

            status = ResponseEntity.ok("You have refused the appointment, reason will be sent to member.");
        } catch (Exception e) {
            status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return status;
    }

    /**
     * Chấp nhận yêu cầu nhận nuôi
     * @param appointments
     * @param staffID
     * @return
     */
    @PutMapping("/accept/{staffID}")
    public ResponseEntity<?> accept(@RequestBody Appointments appointments, @PathVariable String staffID) {
        ResponseEntity<?> status;
        try {
            Appointments appoint = appointmentsService.findById(appointments.getAppointID());
            //chỉnh status của pet
            Pets pets = petsService.findPetById(appoint.getPetID());
            pets.setStatus("Unavailable");
            petsService.addPet(pets);


            appoint.setStatus(true);
            appoint.setStaffID(staffID);
            appointmentsService.save(appoint);
            notificationService.acceptAdoptRequestNotification(appoint, staffID);
            status = ResponseEntity.ok("You have accepted the appointment, notification will be sent to member.");
        }catch (Exception e){
            status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return status;

    }

    @GetMapping("/showUnprocessed")
    public ResponseEntity<?> showUnprocessed() {
        ResponseEntity<?> status;
        try{
            List<Appointments> appointments = appointmentsService.findByStatus(false);
            if(appointments.isEmpty()) {
                status = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No appointments unprocessed found");
            }else{
            status = ResponseEntity.ok(appointments);
            }
        } catch (Exception e) {
            status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return status;
    }

    @GetMapping("/showProcessed")
    public ResponseEntity<?> showProcessed() {
        ResponseEntity<?> status;
        try{
            List<Appointments> appointments = appointmentsService.findByStatus(true);
            if(appointments.isEmpty()) {
                status = ResponseEntity.status(HttpStatus.NOT_FOUND).body("No appointments processed found");
            }
            status = ResponseEntity.ok(appointments);
        } catch (Exception e) {
            status = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return status;
    }
}
