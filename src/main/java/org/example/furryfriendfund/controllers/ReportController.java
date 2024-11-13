package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.example.furryfriendfund.reports.ReportService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;

    }
}
