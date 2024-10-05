package org.example.furryfriendfund.donations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationsService implements IDonationService {

    @Autowired
    IDonationsRepository donationsRepository;

    @Override
    public Donations save(Donations donations) {
        return donationsRepository.save(donations);
    }

    @Override
    public void delete(Donations donations) {
        donationsRepository.deleteById(donations.getDonateID());
    }

    @Override
    public Donations findByDonationId(String donationId) {
        return donationsRepository.findById(donationId).orElse(null);
    }

    @Override
    public List<Donations> findByAccountID(String accountID) {
        return donationsRepository.findByAccountID(accountID);
    }

    @Override
    public List<Donations> findByEventID(String eventID) {
        return donationsRepository.findByEventID(eventID);
    }
}
