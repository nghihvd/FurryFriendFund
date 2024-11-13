package org.example.furryfriendfund.reports;

import java.time.LocalDateTime;
import java.util.List;

public interface IReportService {
    Report save(Report report);
    Report getByID(String reportId);
    void deleteByID(String reportId);
    void deleteByPetID(String petID);
    List<Report> getByPetID(String petID);
    Report findNearestReport(List<Report> reports);
    Report createReport(String petID, LocalDateTime dateReport, byte[] video);
}
