package org.example.furryfriendfund.controllers;


import jakarta.servlet.http.HttpServletRequest;

import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.accounts.AccountsService;

import org.example.furryfriendfund.appointments.Appointments;
import org.example.furryfriendfund.appointments.AppointmentsService;
import org.example.furryfriendfund.jwt.JwtAuthenticationFilter;
import org.example.furryfriendfund.jwt.JwtTokenProvider;
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
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private AppointmentsService appointmentsService;

    //http://localhost:8081/notification/15238822/status?status=true

    /**
     * update status of notication including register, add new pet
     * @param notiID notification id
     * @param status if admin click "Accept" -> status: true, if admin click "Deny" -> status: false
     * @return http status + message
     */
    @PutMapping("/{notiID}/status")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<?> updateRegisStatus(@PathVariable String notiID,
                                               @RequestParam boolean status) {
        Notification find = notificationService.findNoti(notiID);

        // update regisnotification
        if(find.getMessage().contains("want to register system with staff role")) {
            boolean result = notificationService.updateAccountStatusNotification(notiID,status);

            if(result){
                return ResponseEntity.ok().build();
            }
        }
        // update petNotification
        if(find.getMessage().contains("can be added to our shelter??")){
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
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<?> showNoti() {
        List<Notification> list =  notificationService.showNotifications(1);
        if(list.isEmpty()){
            return ResponseEntity.badRequest().body("No notifications found");
        }
        List<Notification> acceptAdopt = new ArrayList<>();
        for(Notification n : list){
            if(n.getPetID() != null && n.isButton_status()&&
                    (n.getMessage().contains("can be added to our shelter??") || n.getMessage().contains("can be deleted??"))){
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
     * @param request to get request to get jwt
     * @return staff notification
     */
    @GetMapping("/showStaffNoti")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> showStaffNoti(HttpServletRequest request) {
        Accounts acc = getAccFromRequest(request);
        if(acc == null){
            return ResponseUtils.createErrorRespone("No account found", null, HttpStatus.UNAUTHORIZED);
        }
        List<Notification> list =  notificationService.showNotifications(2);
        List<Notification> accountNoti  = notificationService.showNotificationsAccountID(acc.getAccountID());

        Set<Notification> setNoti = new HashSet<>(list);
        setNoti.addAll(accountNoti);
    return ResponseUtils.createSuccessRespone("", setNoti);
    }

    /**
     * show member notification which have noti from staff
     * @param request to get request
     * @return set of notification of member account
     */
    @GetMapping("/memberNoti")
    @PreAuthorize("hasAuthority('3')")
    public ResponseEntity<BaseResponse> showMemberNoti(HttpServletRequest request) {
        Accounts acc = getAccFromRequest(request);
        if(acc == null){
            return ResponseUtils.createErrorRespone("No account found", null, HttpStatus.UNAUTHORIZED);
        }
        List<Notification> list =  notificationService.showNotifications(3);
        List<Notification> accountNoti  = notificationService.showNotificationsAccountID(acc.getAccountID());
        for(Notification n: list){
            if(n.getAccountID() == null){
                accountNoti.add(n);
            }
        }
        return ResponseUtils.createSuccessRespone("", accountNoti);
    }


    /**
     * show text notification of admin
     * @return list of notification
     */
    @GetMapping("/otherAdminNoti")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> otherNoti() {

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
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> showRegisNoti() {

        List<Notification> newList = notificationService.showRegisNoti();
        if(newList.isEmpty()){
            return ResponseUtils.createErrorRespone("No notifications found", null, HttpStatus.BAD_REQUEST);
        }
        return ResponseUtils.createSuccessRespone("", newList);
    }

    @GetMapping("/showEventNoti")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> showEventNoti() {
        List<Notification> eventNoti = notificationService.getEventNoti();
        if(eventNoti.isEmpty()){
            return ResponseUtils.createErrorRespone("No notifications found", null, HttpStatus.NOT_FOUND);
        }
        return ResponseUtils.createSuccessRespone("", eventNoti);
    }



    @PostMapping("/remindReport")
    @PreAuthorize("hasAuthority('2')")
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
    @PreAuthorize("hasAuthority('2')")
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
    @PreAuthorize("hasAuthority('1')")
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
    @PreAuthorize("hasAuthority('3') or hasAuthority('2') or hasAuthority('1')")
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

    @DeleteMapping("/deleteNotiByPetID/{notiID}/status")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> deleteNotiByPetID(@PathVariable String notiID,
                                                          @RequestParam boolean status) {
        Notification noti = notificationService.findNoti(notiID);
        if(status) {

            String petID = noti.getPetID();
            boolean result = notificationService.deleteNotificationAboutPetID(petID);
            if (result) {
                petsService.deletePetById(petID);
                return ResponseUtils.createSuccessRespone("Notification deleted", null);
            }
            return ResponseUtils.createErrorRespone("Cannot delete", null, HttpStatus.CONFLICT);
        } else{
            notificationService.deleteNoti(noti.getNotiID());
            return ResponseUtils.createSuccessRespone("Notification deleted", null);
        }
    }



    private Accounts getAccFromRequest(HttpServletRequest request) {
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        if(jwt == null || !jwtTokenProvider.validateToken(jwt)){
            return null;
        }
        String accountID = jwtTokenProvider.getAccountIDFromJWT(jwt);
        return accountsService.getUserById(accountID);

    }

    @PostMapping("/requestDeletePets/{petID}")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> SendRequestDeletePet(@PathVariable String petID) {
        ResponseEntity<BaseResponse> response;
        Pets pet = petsService.findPetById(petID);
        if (pet.getAccountID() == null) {
            Notification noti = notificationService.createDeletePetRequestNotification(petID);
            response = ResponseUtils.createSuccessRespone("Send request to admin success", null);
        } else{
            response = ResponseUtils.createErrorRespone("Cannot send request", null, HttpStatus.NOT_ACCEPTABLE);
        }

        return response;
    }

    @PostMapping("/requestTrust/{appointmentID}/{staffID}")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> requestTrust(@PathVariable String appointmentID, @PathVariable String staffID) {
        ResponseEntity<BaseResponse> response;
        try {
            Appointments appointment = appointmentsService.findById(appointmentID);
            if (appointment != null && appointment.getStaffID().equals(staffID)) {
                if(!notificationService.checkExistTrustRequest(appointment.getPetID())){
                    Notification notification = notificationService.requestTrustNotification(appointment);
                    notificationService.save(notification);
                    response = ResponseUtils.createSuccessRespone("Request will be sent to Admin", null);
                }else{
                    response = ResponseUtils.createErrorRespone("You have sent request, please wait for the response", null, HttpStatus.ALREADY_REPORTED);
                }
            }else if (appointment == null){
                response = ResponseUtils.createErrorRespone("Appointment not found", null, HttpStatus.NOT_FOUND);
            } else{
                response = ResponseUtils.createErrorRespone("You do not have permission for this appointment", null, HttpStatus.FORBIDDEN);
            }
        }catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;

    }
    @GetMapping("/showTrustRequest")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> showTrustRequest() {
        ResponseEntity<BaseResponse> response;
        try {
            List<Notification> list = notificationService.getTrustRequestNotifications();
            if(list.isEmpty()){
                response = ResponseUtils.createErrorRespone("No notifications found", null, HttpStatus.NOT_FOUND);
            }else {
                response = ResponseUtils.createSuccessRespone("", list);
            }
        }catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @DeleteMapping("/refuseTrustRequest/{notificationID}")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> refuseTrustRequest(@PathVariable String notificationID) {
        ResponseEntity<BaseResponse> response =null;
        try {
            Notification notification = notificationService.findNoti(notificationID);
            if (notification != null) {
                String message = notification.getMessage();
                if(message.contains("want to trust report process for baby")){
                    Pets pet = petsService.findPetById(notification.getPetID());
                    String staffID = message.split("_")[1];

                    Notification refuseNoti = notificationService.refuseTrustRequestNotifications(staffID,pet);
                    notificationService.save(refuseNoti);

                    notificationService.deleteNoti(notificationID);

                    response = ResponseUtils.createSuccessRespone("Request is refused", null);
                }else{
                    response = ResponseUtils.createErrorRespone("Invalid request", null, HttpStatus.BAD_REQUEST);
                }
            }else {
                response = ResponseUtils.createErrorRespone("Request not found", null, HttpStatus.NOT_FOUND);
            }
        }catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PutMapping("/banAccountByReason/{accountID}")
    @PreAuthorize("hasAuthority('1')") // Chỉ admin có thể thực hiện
    public ResponseEntity<BaseResponse> updateBanStatus(@PathVariable String accountID, @RequestParam boolean status) {
        Accounts account = accountsService.getUserById(accountID);
        if (account != null) {
            if (status) {
                // Thực hiện hành động ban
                boolean bannedAccount = accountsService.banAccept(accountID);
                if (bannedAccount) {
                    return ResponseUtils.createSuccessRespone("Account banned successfully.", null);
                }
            } else{
                boolean notBanResult = accountsService.notBanAccept(accountID);
                if (notBanResult) {
                    return ResponseUtils.createSuccessRespone("Account not be banned. Notification removed successfully.", null);
                }
            }
        }
        return ResponseUtils.createErrorRespone("Account not found", null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/showReturnPet")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> showReturnPet(){
        List<Notification> returnList = notificationService.findReturnNotification();
        ResponseEntity<BaseResponse> response = ResponseUtils.createSuccessRespone("", returnList) ;
        if(returnList.isEmpty()){
            response = ResponseUtils.createErrorRespone("No notifications found", null, HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
