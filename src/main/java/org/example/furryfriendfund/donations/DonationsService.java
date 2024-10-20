package org.example.furryfriendfund.donations;

import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.events.Events;
import org.example.furryfriendfund.events.EventsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationsService implements IDonationService {

    @Autowired
    IDonationsRepository donationsRepository;
    @Autowired
    private AccountsService accountsService;
    @Autowired
    private EventsService eventsService;

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

    @Override
    public ResponseEntity<BaseResponse> addAccountOnly(Donations donations) {
        String[] strings = donations.getNote().split(" ");
        String accountID="";
        for(int i=0;i<strings.length;i++){
            if(strings[i].contains("Account")){
                accountID = strings[i+1];
            }
        }

        Accounts accounts = accountsService.getUserById(accountID);
        if (accounts != null) {
            donations.setAccountID(accountID);
            accounts.setTotal_donation(accounts.getTotal_donation() + donations.getAmount());
            accountsService.save(accounts);
        }
        save(donations);
        return ResponseUtils.createSuccessRespone("Donate added",null);
    }

    @Override
    public ResponseEntity<BaseResponse> addAccountAndEvent(Donations donations) {
        String[] strings = donations.getNote().split(" ");
        String accountID = "";
        String eventID = "";
        for(int i=0;i<strings.length;i++){
            if(strings[i].contains("Account")){
                accountID = strings[i+1];
            }
            if(strings[i].equals("event")){
                eventID = strings[i+1];
            }
        }
        Accounts accounts = accountsService.getUserById(accountID);
        Events events = eventsService.getEvent(eventID);
        if (accounts != null) {
            donations.setAccountID(accountID);
            accounts.setTotal_donation(accounts.getTotal_donation() + donations.getAmount());
            accountsService.save(accounts);
        }
        if (events != null) {
            donations.setEventID(eventID);
        }
        save(donations);
        return ResponseUtils.createSuccessRespone("Donate added",null);
    }

    @Override
    public ResponseEntity<BaseResponse> addEventOnly(Donations donations) {
        String[] strings = donations.getNote().split(" ");

        String eventID = "";
        for(int i=0;i<strings.length;i++){
            if(strings[i].equals("event")){
                eventID = strings[i+1];
            }
        }
        Events events = eventsService.getEvent(eventID);
        if (events != null) {
            donations.setEventID(eventID);
        }
        save(donations);
        return ResponseUtils.createSuccessRespone("Donate added",null);
    }
}
