package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pet_health_records.Pet_health_record;
import org.example.furryfriendfund.pet_health_records.Pet_health_recordsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/petHealth")
public class PetHealthRecordController {

    private final Pet_health_recordsService pet_health_recordsService;

    public PetHealthRecordController(Pet_health_recordsService pet_health_recordsService) {
        this.pet_health_recordsService = pet_health_recordsService;
    }

    /**
     * add pet health record
     * @param pet_health_record all information staff enter in page
     * @return status
     */
    @PostMapping("/addRecord")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> addPetHealth(@RequestBody Pet_health_record pet_health_record) {
        Pet_health_record result = pet_health_recordsService.addPetHealthRecord(pet_health_record);
        if(result == null){
            return ResponseUtils.createErrorRespone("Cannot add pet health record. Please check again",null, HttpStatus.BAD_REQUEST);
        }
        return ResponseUtils.createSuccessRespone("Success add pet health",result);
    }

    /**
     * show pet health base on petID
     * @param petID have to exist in petList
     * @return status
     */
    @GetMapping("/showPetHealth")
    public ResponseEntity<BaseResponse> getPetHealthRecord(@RequestParam String petID) {
        return ResponseUtils.createSuccessRespone("",pet_health_recordsService.getPetHealthRecordsByID(petID));
    }

    /**
     * update pet health record and not allow to change id
     * @param pet_health_record all infor including change or not change infor
     * @return message success/fail
     */
    @PutMapping("/updateHealth")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> updatePetHealth(@RequestBody Pet_health_record pet_health_record) {
        if(pet_health_recordsService.updatePetHealthRecord(pet_health_record)){
            return ResponseUtils.createSuccessRespone("Success update pet health",pet_health_record);
        }
        return ResponseUtils.createErrorRespone("Failed to update pet health",null, HttpStatus.BAD_REQUEST);
    }


    @DeleteMapping("/deleteHealth/{recordID}")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> deletePetHealth(@PathVariable String recordID) {
        boolean result = pet_health_recordsService.deleteRecord(recordID);
        if(result){
            return ResponseUtils.createSuccessRespone("Success delete pet health",recordID);
        }
        return ResponseUtils.createErrorRespone("Failed to delete pet health",null, HttpStatus.BAD_REQUEST);
    }
}
