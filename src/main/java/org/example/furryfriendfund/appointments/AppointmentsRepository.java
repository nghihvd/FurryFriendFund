package org.example.furryfriendfund.appointments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointments, String> {
    List<Appointments> findByAccountIDAndStatus(String accountID, boolean status);
}
