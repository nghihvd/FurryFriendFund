package org.example.furryfriendfund.pet_health_records;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Pet_health_recordsService implements IPet_health_recordsService {
    @Autowired
    private Pet_health_recordRepository recordRepo;

    /**
     * thêm thông tin 1 buổi khám bệnh của 1 pet
     * @param pet_health_record tất cả thông tin của khám bệnh
     * @return trả ra toàn bộ thông tin nếu ID chưa có
     * còn nếu ID có rồi thì trả ra null
     */
    public Pet_health_record addPetHealthRecord(Pet_health_record pet_health_record) {
        Pet_health_record result = null;
        if(!recordRepo.existsById(pet_health_record.getRecordID())) {
            recordRepo.save(pet_health_record);
            result = pet_health_record;
        }
        return result;
    }
    public boolean deletePetHealthRecord(String recordID) {
        boolean result = false;
        if(recordRepo.existsById(recordID)) {

        }
        return result;
    }

    /**
     * hiển thị toàn bộ pet health record
     * @return danh sách khám bệnh của tất cả các bé
     */
    public List<Pet_health_record> getAllPetHealthRecords() {
        return recordRepo.findAll();
    }


}
