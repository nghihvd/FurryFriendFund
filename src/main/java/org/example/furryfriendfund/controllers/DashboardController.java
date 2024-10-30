package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.accounts.AccountsRepository;
import org.example.furryfriendfund.donations.IDonationsRepository;
import org.example.furryfriendfund.events.EventsRepository;
import org.example.furryfriendfund.pets.PetsRepository;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private PetsRepository petsRepository;

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private IDonationsRepository idonationsRepository;


    @GetMapping("/getAccounts")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> getTotalAccounts() {
       int available = accountsRepository.countAccountsAvailable();
       int banned = accountsRepository.countAccountsBanned();
       int waiting = accountsRepository.countAccountsWaiting();
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
        int admin = accountsRepository.countAdminAccounts();
        int staff = accountsRepository.countStaffAccounts();
        int member = accountsRepository.countMemberAccounts();
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
        int available = petsRepository.countPetAvailable();
        int unavailable = petsRepository.countPetUnavailable();
        int adopted = petsRepository.countPetAdopted();
        int total = available + unavailable + adopted;
        HashMap<String,Integer> map = new HashMap<>();
        map.put("available", available);
        map.put("unavailable", unavailable);
        map.put("adopted", adopted);
        map.put("total", total);
        return ResponseUtils.createSuccessRespone("List pets counting",map);
    }

    @GetMapping("/getEventTotal")
    @PreAuthorize("hasAuthority('1')")
    public ResponseEntity<BaseResponse> getEventsTotal() {
        int waiting = eventsRepository.countWaitingEvents();
        int updating = eventsRepository.countUpdatingEvents();
        int published = eventsRepository.countPublishedEvents();
        int ending = eventsRepository.countEndingEvents();
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
       int anonymous  = idonationsRepository.countAnomyusDonations();
       int eventDonate = idonationsRepository.countEventDonations();
       int total = anonymous + eventDonate;
        HashMap<String,Integer> map = new HashMap<>();
        map.put("anonymous", anonymous);
        map.put("eventDonate", eventDonate);
        map.put("total", total);
        return ResponseUtils.createSuccessRespone("List donate counting",map);
    }

}
