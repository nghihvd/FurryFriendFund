package org.example.furryfriendfund.donations;

import org.example.furryfriendfund.respone.BaseResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IDonationService {
    Donations save(Donations donations);
    void delete(Donations donations);
    Donations findByDonationId(String donationId);
    List<Donations> findByAccountID(String accountID);
    List<Donations> findByEventID(String eventID);
    ResponseEntity<BaseResponse> addAccountOnly(Donations donations);
    ResponseEntity<BaseResponse> addAccountAndEvent(Donations donations);
    ResponseEntity<BaseResponse> addEventOnly(Donations donations);
    double calculateTotalAmount();
    int countEventDonations();
    int countAnomyusDonations();

}
