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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            Donations checkDonate = donationsService.findByDonationId(donation.getDonateID());
            if (checkDonate == null) {
                String note = donation.getNote().toLowerCase();
                if (note.contains("acc") && note.contains("donate fff")) {
                    response = donationsService.addAccountOnly(donation);
                } else if (note.contains("acc") && note.contains("donate")) {
                    response = donationsService.addAccountAndEvent(donation);
                } else if (note.contains("donate") && note.contains("event")) {
                    response = donationsService.addEventOnly(donation);
                } else if (note.contains("donate") && note.contains("fff")) {
                    donationsService.save(donation);
                    response = ResponseUtils.createSuccessRespone("Add donation success", donation);
                } else {
                    response = ResponseUtils.createErrorRespone("Description wrong", null, HttpStatus.BAD_REQUEST);
                }

            } else {
                response = ResponseUtils.createErrorRespone("Donate already added", null, HttpStatus.CONFLICT);
            }

        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    @GetMapping("/getAnonymousDonator")
    public ResponseEntity<BaseResponse> getAnonymousDonator() {
        ResponseEntity<BaseResponse> response;
        try {
            List<Donations> anonymousDonators = donationsService.findByAccountID(null);
            if (anonymousDonators.isEmpty()) {
                response = ResponseUtils.createErrorRespone("No donation found", null, HttpStatus.NOT_FOUND);
            } else {
                response = ResponseUtils.createSuccessRespone("Donations found", anonymousDonators);
            }
        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    @GetMapping("/getDonateByEvent/{eventID}")
    public ResponseEntity<BaseResponse> getDonateByEvent(@PathVariable String eventID) {
        ResponseEntity<BaseResponse> response;
        try {
            List<Donations> donations = donationsService.findByEventID(eventID);
            if (donations.isEmpty()) {
                response = ResponseUtils.createErrorRespone("No donation found", null, HttpStatus.NOT_FOUND);
            } else {
                response = ResponseUtils.createSuccessRespone("Donations found", donations);
            }
        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
