package org.example.furryfriendfund.appointments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointments, String> {
    List<Appointments> findByAccountIDAndStatus(String accountID, boolean status);
    List<Appointments> findByStatus(boolean status);
    @Query(value = "SELECT * FROM appointment a WHERE a.status = true AND a.adopt_status = :adopt_status", nativeQuery = true)
    List<Appointments> findByAdoptStatus(@Param("adopt_status") boolean adopt_status);
}
