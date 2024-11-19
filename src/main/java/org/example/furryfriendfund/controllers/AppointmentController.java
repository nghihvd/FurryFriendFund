package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.OTP.EmailService;
import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.appointments.AppointmentsService;
import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationService;
import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.example.furryfriendfund.reports.Report;
import org.example.furryfriendfund.reports.ReportService;
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
    @Autowired
    private ReportService reportService;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private EmailService emailService;
    /**
     * Gửi yêu cầu nhận nuôi thú cưng
     */
    @PostMapping("/adopt")
    @PreAuthorize("hasAuthority('3')")
    public ResponseEntity<BaseResponse> adopt(@RequestBody Appointments appointments) {
        ResponseEntity<BaseResponse> status;
        boolean check = accountsService.checkBannAcc(appointments.getAccountID());
        if (check) {
            return ResponseUtils.createErrorRespone("account is banned",null,HttpStatus.FORBIDDEN);
        }
         try {
            String appointID = UUID.randomUUID().toString().substring(0, 8);
            appointments.setAppointID(appointID);
            //lấy thông tin thú cưng và account và gửi thông báo cho staff
            Pets pets = petsService.findPetById(appointments.getPetID());
            String accountID = appointments.getAccountID();
            String petID = appointments.getPetID();
            List<Appointments> checkAdoptionInProgress = appointmentsService.findByAccountIDAndApproveStatus(accountID, false);
            /*
               kiểm tra trạng thái của pet nếu Available thì mới cho phép gửi yêu cầu nhận nuôi
             */
            if (pets.getStatus().equals("Available")) {
                            /*kiểm tra xem có yêu cầu nhận  nuôi nào của người dùng này chưa được sử lý ko
              nếu có thì thông báo lại và yêu cầu đợi yêu cầu trc đó đc giải quyết xong đã
            */
                if (checkAdoptionInProgress.isEmpty()) {
                    //chỉnh status của pet
                    pets.setStatus("Waiting");
                    petsService.savePet(pets);

                    //tạo thông báo
                    Notification noti = notificationService.adoptNotification(accountID, petID);
                    notificationService.save(noti);

                    appointmentsService.save(appointments);
                    Accounts acc = accountsService.getUserById(accountID);
                    emailService.sendThankyouAdoptEmail(acc.getEmail(),"Thank you for your adoption!!",pets.getName(),acc.getName());

                    status = ResponseUtils.createSuccessRespone("Send request successfully, please waiting for staff response", appointments);

                } else {
                    status = ResponseUtils.createErrorRespone("Can not send request, you have a pet adoption in process", null, HttpStatus.CONFLICT);
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
                    appoint.setApprove_status(false);
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

     */
    @DeleteMapping("/refuseAdopt/{staffID}")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> refuseAdopt(@RequestBody Appointments appointments, @PathVariable String staffID) {
        ResponseEntity<BaseResponse> status;
        try {
            Appointments appoint = appointmentsService.findById(appointments.getAppointID());
            // trả status về Available
            if (appoint != null) {
                if (!appoint.isAdopt_status()&&staffID.equals(appoint.getStaffID())) {
                    Pets pets = petsService.findPetById(appoint.getPetID());
                    pets.setStatus("Available");
                    petsService.savePet(pets);

                    //tạo thông báo
                    Notification noti = notificationService.resultAdoptNotification(appoint, "refused");
                    notificationService.save(noti);

                    appointmentsService.delete(appoint);
                    status = ResponseUtils.createSuccessRespone("You have refused adopt, pet status will be became available.", appoint);
                } else if(appoint.isAdopt_status()) {
                    status = ResponseUtils.createErrorRespone("This appointment was ended, you can not refuse", null, HttpStatus.CONFLICT);
                } else{
                    status = ResponseUtils.createErrorRespone("You do not have permission for this appointment",null, HttpStatus.CONFLICT);
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

     */
    @PutMapping("/acceptAdopt/{staffID}/{accountID}")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> acceptAdopt(@RequestBody Appointments appointments, @PathVariable String staffID,@PathVariable String accountID) {
        ResponseEntity<BaseResponse> status;
        Accounts acc = accountsService.getUserById(accountID);
        if(acc.getPhone() == null || acc.getCitizen_serial() == null || acc.getConfirm_address() == null
        || acc.getIncome() == 0 || acc.getJob() == null){
                return ResponseUtils.createErrorRespone("Account didn't engough confirmation information", null, HttpStatus.NOT_FOUND);
        }
        try {
            Appointments appoint = appointmentsService.findById(appointments.getAppointID());
            // cập nhật trạng thái của pet
            if (appoint != null) {
                if (!appoint.isAdopt_status()&&staffID.equals(appoint.getStaffID())) {
                Pets pets = petsService.findPetById(appoint.getPetID());
                pets.setStatus("Adopted");
                pets.setAccountID(appoint.getAccountID());
                pets.setAdopt_date(appoint.getDate_time());
                petsService.savePet(pets);

                //tạo thông báo
                Notification noti = notificationService.resultAdoptNotification(appoint, "accepted");
                notificationService.save(noti);

                appoint.setAdopt_status(true);
                appoint.setApprove_status(false);
                appointmentsService.save(appoint);
                status = ResponseUtils.createSuccessRespone("Accepted adopt, pet status will be became unavailable.", appoint);
                } else if(appoint.isAdopt_status()) {
                    status = ResponseUtils.createErrorRespone("This appointment was ended, you cannot do anymore", null, HttpStatus.CONFLICT);
                } else{
                    status = ResponseUtils.createErrorRespone("You do not have permission for this appointment",null, HttpStatus.CONFLICT);
                }
            } else {
                status = ResponseUtils.createErrorRespone("This appointment has been refused, you cannot do anymore", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    @PutMapping("/notTrust/{appointmentID}")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> notTrust(@PathVariable String appointmentID, @RequestParam("reason") String reason) {
        Appointments appointment = appointmentsService.findById(appointmentID);
        boolean checkTrust = notificationService.checkExistTrustRequest(appointment.getPetID());
        if(checkTrust){
            return ResponseUtils.createErrorRespone("Trust request have been already sent. Cannot recall", null, HttpStatus.CONFLICT);
        }
        if (appointment != null) {
            Notification noti = notificationService.findByMessage(appointmentID);
            if (noti != null) {
                notificationService.deleteNoti(noti.getNotiID());
            }
            Pets pets = petsService.getPetByAppointmentID(appointmentID);
            pets.setStatus("Available");
            pets.setAccountID(null);
            pets.setAdopt_date(null);
            petsService.savePet(pets);
            List<Report> reports = reportService.getByPetID(pets.getPetID());
            if (reports != null && !reports.isEmpty()) {
                reportService.deleteByPetID(pets.getPetID());
            }
            Notification notification = notificationService.createNotificationNoTrustForAdmin(appointment.getAppointID(),appointment.getAccountID(), reason);
            notificationService.save(notification);
            return ResponseUtils.createSuccessRespone("Pet status updated, admin notified, pet returned to shelter", notification);
        }
        return ResponseUtils.createErrorRespone("Not found", null, HttpStatus.NOT_FOUND);


    }

    @DeleteMapping("/cancelAppointment")
    @PreAuthorize("hasAuthority('3')")
    public ResponseEntity<BaseResponse> cancelAppointment(@RequestBody Appointments appointments) {
        ResponseEntity<BaseResponse> status;
        try {
            Appointments appoint = appointmentsService.findById(appointments.getAppointID());
            if (appoint != null) {
                if(!appoint.isAdopt_status()){
                    Pets pet = petsService.findPetById(appoint.getPetID());
                    pet.setStatus("Available");
                    if(appoint.getStaffID()!=null) {
                        Notification noti= notificationService.cancelAppointmentNotification(appoint);
                        notificationService.save(noti);
                    }
                    petsService.savePet(pet);
                    appointmentsService.delete(appoint);
                    status = ResponseUtils.createSuccessRespone("Cancelled appointment", null);
                }else{
                    status = ResponseUtils.createErrorRespone("Appointment ended, can not cancel", null, HttpStatus.NOT_FOUND);
                }
            }else {
                status = ResponseUtils.createErrorRespone("Appointment is not exist", null, HttpStatus.NOT_FOUND);
            }
        }  catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    /**
     * Lấy danh sách các appointment chưa đc sử lý

     */
    @GetMapping("/showUnprocessed")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> showUnprocessed() {
        ResponseEntity<BaseResponse> status;
        try {
            List<Appointments> appointments = appointmentsService.findByStatus(false);
            if (appointments.isEmpty()) {
                status = ResponseUtils.createErrorRespone("No appointments unprocessed found", null, HttpStatus.NOT_FOUND);
            } else {
                status = ResponseUtils.createSuccessRespone("", appointments);
            }
        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    @GetMapping("/showAppointmentForMember/{accountID}")
    @PreAuthorize("hasAuthority('3')")
    public ResponseEntity<BaseResponse> showAppointmentForMember(@PathVariable String accountID) {
        ResponseEntity<BaseResponse> status;
        try {
            List<Appointments> appointments = appointmentsService.findByAccountIDAndAdoptStatus(accountID,false);
            if (appointments.isEmpty()) {
                status = ResponseUtils.createErrorRespone("No appointments in progress found", null, HttpStatus.NOT_FOUND);
            }else{
                status = ResponseUtils.createSuccessRespone("", appointments);
            }
        }  catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }


    /**
     * lấy danh sách những appointment đang đợi tới ngày gặp mặt

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
     * lấy những danh sách các appointment đã hoàn thành và được tin tưởng

     */
    @GetMapping("/showApproved")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> showEnded() {
        ResponseEntity<BaseResponse> status;
        try {
            List<Appointments> appointments = appointmentsService.findByApproveStatus(true);
            if (appointments.isEmpty()) {
                status = ResponseUtils.createErrorRespone("No appointments approved found", null, HttpStatus.NOT_FOUND);
            } else {
                status = ResponseUtils.createSuccessRespone("Appointments approved", appointments);
            }
        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    /**
     * hiển thị các appointment  mà pet đã được   nhận nuôi nhưng vẫn đang trong quá trình báo cáo
     */
    @GetMapping("/showReliableProcess")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> showReliableProcess() {
        ResponseEntity<BaseResponse> status;
        try {
            List<Appointments> appointments = appointmentsService.findByApproveStatus(false);
            if (appointments.isEmpty()) {
                status = ResponseUtils.createErrorRespone("No appointments approved found", null, HttpStatus.NOT_FOUND);
            } else {
                status = ResponseUtils.createSuccessRespone("Appointments approved", appointments);
            }
        } catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    @PutMapping("/trust/{notificationID}")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> trust(@PathVariable String notificationID) {
        ResponseEntity<BaseResponse> status;
        try {
            Notification notification = notificationService.findNoti(notificationID);
            if(notification!=null) {
                String message = notification.getMessage();
                if(message.contains("want to trust report process for baby")){
                    Pets pet = petsService.findPetById(notification.getPetID());
                    String staffID = message.split("_")[1];
                    List<Appointments> appointments = appointmentsService.findByPetID(pet.getPetID());

                    if (!appointments.isEmpty()) {
                        Appointments appointment = appointments.get(0);
                        appointment.setApprove_status(true);
                        appointmentsService.save(appointment);
                        pet.setStatus("Trusted");
                        petsService.savePet(pet);

                        Notification memberNoti = notificationService.acceptTrustRequestNotificationsForMember(pet.getAccountID(), pet);
                        Notification staffNoti = notificationService.acceptTrustRequestNotificationsForStaff(appointment.getStaffID(),pet);
                        notificationService.save(memberNoti);
                        notificationService.save(staffNoti);

                        notificationService.deleteNoti(notificationID);

                        reportService.deleteByPetID(pet.getPetID());
                        status = ResponseUtils.createSuccessRespone("Request is accepted", null);
                    } else{
                        status = ResponseUtils.createErrorRespone("Pet is not adopted", null, HttpStatus.NOT_FOUND);
                    }

                }else{
                    status = ResponseUtils.createErrorRespone("Invalid request", null, HttpStatus.BAD_REQUEST);
                }
            }else {
                status = ResponseUtils.createErrorRespone("Request not found", null, HttpStatus.NOT_FOUND);
            }
        }catch (Exception e) {
            status = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    @GetMapping("/checkReliableProcessForMember/{acountID}")
    @PreAuthorize("hasAuthority('3')")
    public ResponseEntity<BaseResponse> checkReliableProcessForMember(@PathVariable String acountID) {
        ResponseEntity<BaseResponse> status = null;
        try {
            List<Appointments> appointments = appointmentsService.findByApproveStatus(false);
            for (Appointments appointment : appointments) {
                if(appointment.getAccountID().equals(acountID)){
                    status = ResponseUtils.createSuccessRespone("", petsService.findPetById(appointment.getPetID()));
                    break;
                }
            }
            if(status==null){
                status = ResponseUtils.createErrorRespone("There is not reliable process", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return  status;
    }


}
