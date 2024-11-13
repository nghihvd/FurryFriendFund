package org.example.furryfriendfund.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
