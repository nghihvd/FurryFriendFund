package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pet_health_records.IPet_health_recordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController("/petshealth")
public class HealthRecordController {
    @Autowired
    private IPet_health_recordsService petHealthRecordsService;


}
