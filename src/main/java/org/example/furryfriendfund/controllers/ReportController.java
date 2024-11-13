package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.example.furryfriendfund.reports.Report;
import org.example.furryfriendfund.reports.ReportService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    ReportService reportService;
    @Autowired
    PetsService petsService;

    @PostMapping("/{petID}")
    @PreAuthorize("hasAuthority('3') ")
    public ResponseEntity<BaseResponse> reportPet(@RequestParam("videoFile") MultipartFile videoFile,
                                                  @PathVariable String petID) {
        ResponseEntity<BaseResponse> response =null;
        try {
            Pets pet = petsService.findPetById(petID);
            if(pet != null) {
                LocalDateTime dateAdopt = pet.getAdopt_date();
                LocalDateTime now = LocalDateTime.now();
                long weekPassed = ChronoUnit.WEEKS.between(dateAdopt, now)+1;
                List<Report> petReports = reportService.getByPetID(petID);
                Report report =reportService.findNearestReport(petReports) ;
                if(weekPassed==petReports.size()) {
                    report.setVideo(videoFile.getBytes());
                    report.setDate_report(now);
                }else {
                    if(report==null) {
                        report = reportService.createReport(petID,now,videoFile.getBytes());
                    } else{
                        long nearestReportWeekPassed = ChronoUnit.WEEKS.between(pet.getAdopt_date(), report.getDate_report())+1;
                        if(nearestReportWeekPassed==weekPassed) {
                            report.setVideo(videoFile.getBytes());
                            report.setDate_report(now);
                        }else{
                            report = reportService.createReport(petID,now,videoFile.getBytes());
                        }
                    }
                }
                reportService.save(report);
                response = ResponseUtils.createSuccessRespone("Video upload successfully",null);

            }else{
                response = ResponseUtils.createErrorRespone("Pet not found",null,HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping("/getPetReports/{petID}")
    @PreAuthorize("hasAuthority('2') ")
    public ResponseEntity<BaseResponse> getPetReports(@PathVariable String petID) {
        ResponseEntity<BaseResponse> response;
        try {
            Pets pet = petsService.findPetById(petID);
            if(pet != null) {
                if(pet.getAdopt_date() != null) {
                    List<Report> petReports = reportService.getByPetID(petID);
                    response = ResponseUtils.createSuccessRespone("Pet Reports",petReports);
                }else {
                    response = ResponseUtils.createErrorRespone("This pet is not adopted, so has not report",null,HttpStatus.NOT_FOUND);
                }
            }else{
                response = ResponseUtils.createErrorRespone("Pet not found",null,HttpStatus.NOT_FOUND);
            }
        }catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
