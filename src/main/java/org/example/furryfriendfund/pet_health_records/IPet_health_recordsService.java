package org.example.furryfriendfund.pet_health_records;

import java.util.List;

public interface IPet_health_recordsService {
     Pet_health_record addPetHealthRecord(Pet_health_record pet_health_record);
     boolean deleteRecord(String recordID);
     List<Pet_health_record> getPetHealthRecordsByID(String petID);
     boolean updatePetHealthRecord(Pet_health_record pet_health_record);
     boolean deletePetHealthRecord(String petID);
}
