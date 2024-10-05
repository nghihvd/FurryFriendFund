package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.donations.Donations;
import org.example.furryfriendfund.donations.DonationsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/donation")
public class DonationController {
    @Autowired
    private DonationsService donationsService;

    @Autowired
    private AccountsService accountsService;

    @PostMapping("/add")
    public ResponseEntity<BaseResponse> addDonation(@RequestBody Donations donation) {
        ResponseEntity<BaseResponse> response;
        try {
            Accounts accounts = accountsService.getUserById(donation.getAccountID());
            if(accounts != null) {
                accounts.setTotal_donation(accounts.getTotal_donation() + donation.getAmount());
                accountsService.save(accounts);
            }
            donationsService.save(donation);
            response = ResponseUtils.createSuccessRespone("Add donation success", donation);
        }catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(),null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
