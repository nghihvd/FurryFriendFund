package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pet_health_records.IPet_health_recordsService;
import org.example.furryfriendfund.pet_health_records.Pet_health_record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/petshealth")
public class HealthRecordController {
    @Autowired
    private IPet_health_recordsService petHealthRecordsService;

    /**
     * thêm 1 lần khám bệnh của pets và gửi thông báo
     * @param pet_health_record thông tin khám bệnh mới của pets
     * @return trả ra all thông tin bệnh cuủa pet mơ thêm vào
     */
    @PostMapping("/add")
    public ResponseEntity<?> addPetHealth(@RequestBody  Pet_health_record pet_health_record){
        pet_health_record.setRecordID(UUID.randomUUID().toString().substring(0, 8));
        Pet_health_record newRecord = petHealthRecordsService.addPetHealthRecord(pet_health_record);
        if(newRecord == null){
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Record already exists");
        }
        return ResponseEntity.created(URI.create("/petshealth/add")).body(pet_health_record);

    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePetHealth(@RequestBody String recordID){

        return null;
    }

}
