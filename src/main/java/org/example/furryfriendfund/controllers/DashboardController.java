package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.donations.DonationsService;
import org.example.furryfriendfund.events.EventsService;
import org.example.furryfriendfund.pets.PetsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private PetsService petsService;
    @Autowired
    private AccountsService accountsService;
    @Autowired
    private EventsService eventsService;
    @Autowired
    private DonationsService donationsService;


    @GetMapping("/getAccounts")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> getTotalAccounts() {
       int available = accountsService.countNote("Available");
       int banned = accountsService.countNote("Banned");
       int waiting = accountsService.countNote("Waiting");
       int total = available + banned + waiting;
        HashMap<String,Integer> map = new HashMap<>();
        map.put("available", available);
        map.put("banned", banned);
        map.put("waiting", waiting);
        map.put("total", total);
        return ResponseUtils.createSuccessRespone("List note account",map);
    }

    @GetMapping("/getRoleAccounts")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> getRoleAccounts() {
        int admin = accountsService.countRoleAccount(1);
        int staff = accountsService.countRoleAccount(2);
        int member = accountsService.countRoleAccount(3);
        int total = admin + staff + member;
        HashMap<String,Integer> map = new HashMap<>();
        map.put("admin", admin);
        map.put("staff",staff);
        map.put("member", member);
        map.put("total", total);
        return ResponseUtils.createSuccessRespone("List role accounts",map);
    }
    @GetMapping("/getPetTotal")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> getPetTotal() {
        int available = petsService.countPet("Available");
        int unavailable = petsService.countPet("Unavailable");
        int adopted = petsService.countPet("Adopted");
        int trusted = petsService.countPet("Trusted");
        int processing = petsService.countPet("Processing");
        int total = available + unavailable + adopted + trusted;
        HashMap<String,Integer> map = new HashMap<>();
        map.put("available", available);
        map.put("unavailable", unavailable);
        map.put("adopted", adopted);
        map.put("trusted",trusted);
        map.put("processing", processing);
        map.put("total", total);
        return ResponseUtils.createSuccessRespone("List pets counting",map);
    }

    @GetMapping("/getEventTotal")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> getEventsTotal() {
        int waiting = eventsService.countEvents("Waiting");
        int updating = eventsService.countEvents("Updating");
        int published = eventsService.countEvents("Published");
        int ending = eventsService.countEvents("Ending");
        int total = waiting + updating + published + ending;
        HashMap<String,Integer> map = new HashMap<>();
        map.put("waiting", waiting);
        map.put("updating", updating);
        map.put("published", published);
        map.put("ending", ending);
        map.put("total", total);
        return ResponseUtils.createSuccessRespone("List events counting",map);
    }

    @GetMapping("/getDonateTotal")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> getDonateTotal() {
       int anonymous  = donationsService.countAnomyusDonations();
       int eventDonate = donationsService.countEventDonations();
       int total = anonymous + eventDonate;
        HashMap<String,Integer> map = new HashMap<>();
        map.put("anonymous", anonymous);
        map.put("eventDonate", eventDonate);
        map.put("total", total);
        return ResponseUtils.createSuccessRespone("List donate counting",map);
    }

}
