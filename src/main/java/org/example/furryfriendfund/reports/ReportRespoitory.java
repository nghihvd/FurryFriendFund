package org.example.furryfriendfund.reports;

import org.example.furryfriendfund.pets.Pets;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRespoitory extends JpaRepository<Report, String> {
}
