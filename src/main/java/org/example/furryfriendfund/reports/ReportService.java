package org.example.furryfriendfund.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService implements IReportService {
    @Autowired
    ReportRespoitory reportRespoitory;


    @Override
    public Report save(Report report) {
        return reportRespoitory.save(report);
    }

    @Override
    public Report getByID(String reportId) {
        return reportRespoitory.findById(reportId).orElse(null);
    }

    @Override
    public void deleteByID(String reportId) {
        reportRespoitory.deleteById(reportId);
    }

    @Override
    public void deleteByPetID(String petID) {
        reportRespoitory.deleteByPetID(petID);
    }

    @Override
    public List<Report> getByPetID(String petID) {
        return reportRespoitory.findByPetID(petID);
    }

    @Override
    public Report findNearestReport(List<Report> reports) {
        LocalDateTime now = LocalDateTime.now();
        return reports.stream()
                .min((o1, o2) -> {
                    long diff1 = Duration.between(now, o1.getDate_report()).abs().toMillis();
                    long diff2 = Duration.between(now, o2.getDate_report()).abs().toMillis();
                    return Long.compare(diff1, diff2);
                })
                .orElse(null);
    }

    @Override
    public Report createReport(String petID, LocalDateTime dateReport, byte[] video) {
        return new Report(UUID.randomUUID().toString().substring(0, 8),petID,dateReport,video);
    }
}
