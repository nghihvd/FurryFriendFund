package org.example.furryfriendfund.appointments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointments, String> {
    @Query(value = "SELECT * FROM appointment a WHERE a.accountID = :accountID AND a.adopt_status = :adopt_status", nativeQuery = true)
    List<Appointments> findByAccountIDAndAdoptStatus(@Param("accountID") String accountID,@Param("adopt_status") boolean adopt_status);
    List<Appointments> findByStatus(boolean status);
    @Query(value = "SELECT * FROM appointment a WHERE a.status = true AND a.adopt_status = :adopt_status", nativeQuery = true)
    List<Appointments> findByAdoptStatus(@Param("adopt_status") boolean adopt_status);
    @Query(value = "SELECT * FROM appointment a WHERE a.status = true AND a.adopt_status = true AND a.approve_status = :approve_status", nativeQuery = true)
    List<Appointments> findByApproveStatus(@Param("approve_status") boolean approve_status);
    @Query(value = "SELECT * FROM appointment a WHERE a.accountID = :accountID AND a.approve_status = :approve_status", nativeQuery = true)
    List<Appointments> findByAccountIDAndApproveStatus(@Param("accountID") String accountID,@Param("approve_status") boolean approve_status);
    List<Appointments> findByPetID(String petID);
    List<Appointments> findByAccountID(String accountID);

}
