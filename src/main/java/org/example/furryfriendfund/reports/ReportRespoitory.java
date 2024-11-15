package org.example.furryfriendfund.reports;

import org.example.furryfriendfund.pets.Pets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReportRespoitory extends JpaRepository<Report, String> {
    List<Report> findByPetID(String petID);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reports a WHERE a.petID = :petID", nativeQuery = true)
    void deleteByPetID(@Param("petID")String petID);
}
