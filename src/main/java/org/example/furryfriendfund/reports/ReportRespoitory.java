package org.example.furryfriendfund.reports;

import org.example.furryfriendfund.pets.Pets;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRespoitory extends JpaRepository<Report, String> {
    List<Report> findByPetID(String petID);
    void deleteByPetID(String petID);
}
