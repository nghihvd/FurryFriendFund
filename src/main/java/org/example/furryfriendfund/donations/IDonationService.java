package org.example.furryfriendfund.donations;

import java.util.List;

public interface IDonationService {
    Donations save(Donations donations);
    void delete(Donations donations);
    Donations findByDonationId(String donationId);
    List<Donations> findByAccountID(String accountID);
    List<Donations> findByEventID(String eventID);
}
