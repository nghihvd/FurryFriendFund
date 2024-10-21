package org.example.furryfriendfund.donations;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDonationsRepository extends JpaRepository<Donations, String> {
    List<Donations> findByAccountID(String accountID);
    List<Donations> findByEventID(String eventID);
}
