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
        String[] strings = donations.getNote().toLowerCase().split(" ");
        String accountID="";
        for(int i=0;i<strings.length;i++){
            accountID = getString(strings, accountID, i);
        }

        Accounts accounts = accountsService.getAccByIdIgnoreCase(accountID);
        if (accounts != null) {
            donations.setAccountID(accounts.getAccountID());
        }
        save(donations);
        if(accounts!=null){
            accounts.setTotal_donation(donationsRepository.calculateTotalAccount(donations.getAccountID()));
            accountsService.save(accounts);
        }
        return ResponseUtils.createSuccessRespone("Donate added",null);
    }

    @Override
    public ResponseEntity<BaseResponse> addAccountAndEvent(Donations donations) {
        String[] strings = donations.getNote().toLowerCase().split(" ");
        String accountID = "";
        String eventID = "";
        for(int i=0;i<strings.length;i++){
            accountID = getString(strings, accountID, i);
            if(strings[i].contains("donate")){
                if(strings[i].length()>"donate".length()&&strings[i].endsWith("donate")){
                    eventID = strings[i+1];
                    if(eventID.length()>8){
                    eventID = eventID.substring(0,8);
                    }
                } else if(strings[i].length()>"donate".length()&&strings[i].startsWith("donate")){
                    eventID = strings[i].replace("donate","");
                    if(eventID.length()>8){
                        eventID = eventID.substring(0,8);
                    }
                }else{
                    eventID = strings[i+1];
                    if(eventID.length()>8){
                        eventID = eventID.substring(0,8);
                    }
                }
                break;
            }
        }
        Accounts accounts = accountsService.getAccByIdIgnoreCase(accountID);
        Events events = eventsService.getEvent(eventID);
        if (accounts != null) {
            donations.setAccountID(accounts.getAccountID());
        }
        if (events != null) {
            donations.setEventID(eventID);
        }
        save(donations);
        if(accounts!=null){
            accounts.setTotal_donation(donationsRepository.calculateTotalAccount(donations.getAccountID()));
            accountsService.save(accounts);
        }
        if (events!=null){
            events.setTotal_donation(donationsRepository.calculateTotalEvent(donations.getEventID()));
            eventsService.save(events);
        }
        return ResponseUtils.createSuccessRespone("Donate added",null);
    }



    @Override
    public ResponseEntity<BaseResponse> addEventOnly(Donations donations) {
        String[] strings = donations.getNote().toLowerCase().split(" ");

        String eventID = "";
        for(int i=0;i<strings.length;i++){
            if(strings[i].contains("event")){
                eventID = strings[i+1];
                if(eventID.length()>8){
                    eventID = eventID.substring(0,8);
                }
                break;
            }
        }
        Events events = eventsService.getEvent(eventID);
        if (events != null) {
            donations.setEventID(eventID);
        }
        save(donations);
        if (events!=null){
            events.setTotal_donation(donationsRepository.calculateTotalEvent(donations.getEventID()));
            eventsService.save(events);
        }
        return ResponseUtils.createSuccessRespone("Donate added",null);
    }

    @Override
    public int countEventDonations() {
        return donationsRepository.countEventDonations();
    }

    @Override
    public int countAnomyusDonations() {
        return donationsRepository.countAnomyusDonations();
    }

    @Override
    public double calculateTotalAmount() {
        return donationsRepository.calculateTotalAmount();
    }

    private String getString(String[] strings, String accountID, int i) {
        if(strings[i].contains("acc")){
            accountID = strings[i+1];
            if(accountID.endsWith("donate")&&!accountID.equals("donate")){
                accountID = accountID.replaceFirst("(?s)(.*)donate$", "$1");
            }else if(!strings[i].endsWith("acc")){
                String[] strings2 = strings[i].replace("acc", " ").split(" ");
                if(strings2.length>1) {
                    accountID = strings2[1];
                }else accountID = strings2[0];
            }
        }
        return accountID;
    }

}
