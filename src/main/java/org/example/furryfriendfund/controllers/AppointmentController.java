package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.appointments.AppointmentsService;
import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationService;
import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AppointmentsService appointmentsService;

    @Autowired
    private PetsService petsService;

    /**
     * Gửi yêu cầu nhận nuôi thú cưng
     *
     * @param appointments
     * @return
     */
    @PostMapping("/adopt")
    @PreAuthorize("hasAuthority('3')")
    public ResponseEntity<BaseResponse> adopt(@RequestBody Appointments appointments) {
        ResponseEntity<BaseResponse> status;


        try {
            String appointID = UUID.randomUUID().toString().substring(0, 8);
            appointments.setAppointID(appointID);
            //lấy thông tin thú cưng và account và gửi thông báo cho staff
            Pets pets = petsService.findPetById(appointments.getPetID());
            String accountID = appointments.getAccountID();
            String petID = appointments.getPetID();
            List<Appointments> checkApointInProgress = appointmentsService.findByAccountIDAndAdoptStatus(accountID, false);
            /*
               kiểm tra trạng thái của pet nếu Available thì mới cho phép gửi yêu cầu nhận nuôi
             */
            if (pets.getStatus().equals("Available")) {
                            /*kiểm tra xem có yêu cầu nhận  nuôi nào của người dùng này chưa được sử lý ko
              nếu có thì thông báo lại và yêu cầu đợi yêu cầu trc đó đc giải quyết xong đã
            */
                if (checkApointInProgress.isEmpty()) {
                    //chỉnh status của pet
                    pets.setStatus("Waiting");
                    petsService.savePet(pets);

                    //tạo thông báo
                    Notification noti = notificationService.adoptNotification(accountID, petID);
                    notificationService.save(noti);

                    appointmentsService.save(appointments);
                    status = ResponseUtils.createSuccessRespone("Send request successfully, please waiting for staff response", appointments);

                } else {
                    status = ResponseUtils.createErrorRespone("You have a request in progress, please wait until that request processed", null, HttpStatus.CONFLICT);
                }
            } else
                status = ResponseUtils.createErrorRespone("This pet are in progress adopt, please waiting for result or choose another one", null, HttpStatus.CONFLICT);

        } catch (Exception e) {
            Pets pets = petsService.findPetById(appointments.getPetID());
            pets.setStatus("Available");
            petsService.savePet(pets);
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    /**
     * Từ chối cuộc hẹn cho yêu cầu nhận nuôi
     *
     * @param appointments
     * @param reason
     * @return
     */
    @DeleteMapping("/refuse/{reason}")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> refuse(@RequestBody Appointments appointments, @PathVariable String reason) {
        ResponseEntity<BaseResponse> status;
        try {
            Appointments appoint = appointmentsService.findById(appointments.getAppointID());
            if (appoint != null) {
                if (!appoint.isStatus()) {
                    //trả status về lại ban đầu
                    Pets pets = petsService.findPetById(appoint.getPetID());
                    pets.setStatus("Available");
                    petsService.savePet(pets);


                    appointmentsService.delete(appoint);
                    Notification noti = notificationService.refuseAdoptRequestNotification(appoint, reason);
                    notificationService.save(noti);

                    status = ResponseUtils.createSuccessRespone("You have refused adopt, pet status will be became available.", appoint);
                } else {
                    status = ResponseUtils.createErrorRespone("Another staff accepted this appointment, you can not refuse", null, HttpStatus.CONFLICT);
                }
            } else {
                status = ResponseUtils.createErrorRespone("This appointment has been refused, you cannot do anymore", null, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    /**
     * Chấp nhận cuộc hẹn cho yêu cầu nhận nuôi
     *
     * @param appointments
     * @param staffID
     * @return
     */
    @PutMapping("/accept/{staffID}")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> accept(@RequestBody Appointments appointments, @PathVariable String staffID) {
        ResponseEntity<BaseResponse> status;
        try {
            Appointments appoint = appointmentsService.findById(appointments.getAppointID());

            if (appoint != null) {
                if (!appoint.isStatus()) {
                    appoint.setStatus(true);
                    appoint.setStaffID(staffID);
                    appointmentsService.save(appoint);
                    Notification noti = notificationService.acceptAdoptRequestNotification(appoint, staffID);
                    notificationService.save(noti);
                    status = ResponseUtils.createSuccessRespone("You have accepted adopt, member will be notified to come on time.", appoint);
                } else {
                    status = ResponseUtils.createErrorRespone("Another staff accepted this appointment, you can not accept again", null, HttpStatus.CONFLICT);
                }
            } else {
                status = ResponseUtils.createErrorRespone("This appointment has been refused, you cannot do anymore", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;

    }

    /**
     * Từ chối cho member nhận nuôi
     *
     * @param appointments
     * @return
     */
    @DeleteMapping("/refuseAdopt")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> refuseAdopt(@RequestBody Appointments appointments) {
        ResponseEntity<BaseResponse> status;
        try {
            Appointments appoint = appointmentsService.findById(appointments.getAppointID());
            // trả status về Available
            if (appoint != null) {
                if (!appoint.isAdopt_status()) {
                    Pets pets = petsService.findPetById(appoint.getPetID());
                    pets.setStatus("Available");
                    petsService.savePet(pets);

                    //tạo thông báo
                    Notification noti = notificationService.resultAdoptNotification(appoint, "refused");
                    notificationService.save(noti);

                    appointmentsService.delete(appoint);
                    status = ResponseUtils.createSuccessRespone("You have refused adopt, pet status will be became available.", appoint);
                } else {
                    status = ResponseUtils.createErrorRespone("This appointment was ended, you can not refuse", null, HttpStatus.CONFLICT);
                }
            } else {
                status = ResponseUtils.createErrorRespone("This appointment has been refused, you cannot do anymore", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    /**
     * Đồng ý cho member nhận nuôi
     *
     * @param appointments
     * @return
     */
    @PutMapping("/acceptAdopt")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> acceptAdopt(@RequestBody Appointments appointments) {
        ResponseEntity<BaseResponse> status;
        try {
            Appointments appoint = appointmentsService.findById(appointments.getAppointID());
            // cập nhật trạng thái của pet
            if (appoint != null) {
                Pets pets = petsService.findPetById(appoint.getPetID());
                pets.setStatus("Unavailable");
                pets.setAccountID(appoint.getAccountID());
                pets.setAdopt_date(appoint.getDate_time());
                petsService.savePet(pets);

                //tạo thông báo
                Notification noti = notificationService.resultAdoptNotification(appoint, "accepted");
                notificationService.save(noti);

                appoint.setAdopt_status(true);
                appointmentsService.save(appoint);
                status = ResponseUtils.createSuccessRespone("Accepted adopt, pet status will be became unavailable.", appoint);
            } else {
                status = ResponseUtils.createErrorRespone("This appointment has been refused, you cannot do anymore", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }


    /**
     * Lấy danh sách các appointment chưa đc sử lý
     *
     * @return
     */
    @GetMapping("/showUnprocessed")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> showUnprocessed() {
        ResponseEntity<BaseResponse> status;
        try {
            List<Appointments> appointments = appointmentsService.findByStatus(false);
            if (appointments.isEmpty()) {
                status = ResponseUtils.createErrorRespone("No appointments processed found", null, HttpStatus.NOT_FOUND);
            } else {
                status = ResponseUtils.createSuccessRespone("", appointments);
            }
        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    /**
     * lấy danh sách những appointment đang đợi tới ngày gặp mặt
     *
     * @return
     */
    @GetMapping("/showNotHappenedYet")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> showNotHappenedYet() {
        ResponseEntity<BaseResponse> status;
        try {
            List<Appointments> appointments = appointmentsService.findByAdoptStatus(false);
            if (appointments.isEmpty()) {
                status = ResponseUtils.createErrorRespone("No appointments processed found", null, HttpStatus.NOT_FOUND);
            } else {
                status = ResponseUtils.createSuccessRespone("", appointments);
            }
        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    /**
     * lấy những danh sách các appointment đã hoàn thành
     *
     * @return
     */
    @GetMapping("/showEnded")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> showEnded() {
        ResponseEntity<BaseResponse> status;
        try {
            List<Appointments> appointments = appointmentsService.findByAdoptStatus(true);
            if (appointments.isEmpty()) {
                status = ResponseUtils.createErrorRespone("No appointments ended found", null, HttpStatus.NOT_FOUND);
            } else {
                status = ResponseUtils.createSuccessRespone("Appointment ended", appointments);
            }
        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }
}
