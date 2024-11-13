package org.example.furryfriendfund.reports;

import java.util.List;

public interface IReportService {
    Report save(Report report);
    Report getByID(String reportId);
    void deleteByID(String reportId);
    void deleteByPetID(String petID);
    List<Report> getByPetID(String petID);
}
