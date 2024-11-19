package org.example.furryfriendfund.donations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IDonationsRepository extends JpaRepository<Donations, String> {
    List<Donations> findByAccountID(String accountID);
    List<Donations> findByEventID(String eventID);

    @Query("SELECT COUNT(d) FROM Donations d WHERE d.eventID is null ")
    int countAnomyusDonations();

    @Query("SELECT COUNT(d) FROM Donations d WHERE d.eventID is not null ")
    int countEventDonations();
    @Query(value = "SELECT SUM(amount) FROM donations", nativeQuery = true)
    double calculateTotalAmount();

    @Query(value = "SELECT SUM(amount) FROM donations d WHERE d.eventID = :eventID", nativeQuery = true)
    double calculateTotalEvent(@Param("eventID") String eventID);

    @Query(value = "SELECT SUM(amount) FROM donations d WHERE d.accountID = :accountID", nativeQuery = true)
    double calculateTotalAccount(@Param("accountID") String accountID);

}
