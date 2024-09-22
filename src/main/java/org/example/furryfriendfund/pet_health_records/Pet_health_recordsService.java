package org.example.furryfriendfund.pet_health_records;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Pet_health_recordsService implements IPet_health_recordsService {
    @Autowired
    private Pet_health_recordRepository recordRepo;

    public boolean addPetHealthRecord(Pet_health_record pet_health_record) {
        boolean result = false;
        if(recordRepo.findById(pet_health_record.getRecordID()) != null) {
            recordRepo.save(pet_health_record);
            result = true;
        }
        return result;
    }

}
