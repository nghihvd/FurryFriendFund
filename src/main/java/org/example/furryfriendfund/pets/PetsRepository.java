package org.example.furryfriendfund.pets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetsRepository extends JpaRepository<Pets, String> {
    @Query("select a from Pets a where REPLACE(LOWER(a.name), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:name, ' ', ''), '%'))")
    List<Pets> findByNameIgnoreCaseAndTrimmed(@Param("name") String name);
    List<Pets> getPetsByAccountID(String accountID);



    @Query("SELECT COUNT(p) FROM Pets p WHERE p.accountID is not null and p.status = :status ")
    int countPetStatus(@Param("status") String status);

    @Query("select a from Pets a where a.petID = :id")
    Pets getPetByPetID(@Param("id") String id);

    @Query("SELECT p FROM Pets p JOIN Appointments a ON p.petID = a.petID WHERE a.appointID = :appointmentID")
    Pets getPetByAppointmentID(String appointmentID);
}

