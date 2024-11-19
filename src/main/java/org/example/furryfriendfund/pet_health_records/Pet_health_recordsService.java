package org.example.furryfriendfund.pet_health_records;

import org.example.furryfriendfund.notification.NotificationService;
import org.example.furryfriendfund.pets.PetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class Pet_health_recordsService implements IPet_health_recordsService {
    @Autowired
    private Pet_health_recordRepository recordRepo;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PetsService petsService;

    /**
     * thêm thông tin 1 buổi khám bệnh của 1 pet
     * @param pet_health_record tất cả thông tin của khám bệnh
     * @return trả ra toàn bộ thông tin nếu ID chưa có
     * còn nếu ID có rồi thì trả ra null
     */
    @Override
    public Pet_health_record addPetHealthRecord(Pet_health_record pet_health_record) {
        Pet_health_record result = null;
        pet_health_record.setRecordID(UUID.randomUUID().toString().substring(0, 8));
        if(petsService.findPetById(pet_health_record.getPetID()) == null){
            return result;
        }
        if(!recordRepo.existsById(pet_health_record.getRecordID())) {
            recordRepo.save(pet_health_record);
            notificationService.createHealthNoti(pet_health_record);
            result = pet_health_record;
        }
        return result;    }

    @Override
    public boolean deleteRecord(String recordID) {
        boolean result = false;
        if(recordRepo.existsById(recordID)) {
            notificationService.deleteHealthNoti(recordRepo.findById(recordID).get());
            recordRepo.deleteById(recordID);
            result = true;
        }
        return result;
    }

    @Override
    /**
     * hiển thị toàn bộ pet health record
     * @return danh sách khám bệnh của tất cả các bé
     */
    public List<Pet_health_record> getPetHealthRecordsByID(String petID) {
        return recordRepo.findPet_health_recordByPetID(petID);
    }

    @Override
    public boolean updatePetHealthRecord(Pet_health_record pet_health_record) {
        boolean result = false;
        if(recordRepo.existsById(pet_health_record.getRecordID())) {
            Pet_health_record record = recordRepo.findById(pet_health_record.getRecordID()).orElse(null);
            if(record.getVeterinary_fee() != pet_health_record.getVeterinary_fee()) {
                record.setVeterinary_fee(pet_health_record.getVeterinary_fee());
            }
            if(!Objects.equals(record.getIllness_name(), pet_health_record.getIllness_name())) {
                record.setIllness_name(pet_health_record.getIllness_name());
            }
            if (!Objects.equals(record.getNote(), pet_health_record.getNote())) {
                record.setNote(pet_health_record.getNote());
            }
            if (record.getCheck_in_date() != pet_health_record.getCheck_in_date()) {
                record.setCheck_in_date(pet_health_record.getCheck_in_date());
            }
            if (record.getCheck_out_date() != pet_health_record.getCheck_out_date()) {
                record.setCheck_out_date(pet_health_record.getCheck_out_date());
            }
            if(record.getVeterinarian_name() != pet_health_record.getVeterinarian_name()) {
                record.setVeterinarian_name(pet_health_record.getVeterinarian_name());
            }
            recordRepo.save(record);
            notificationService.updateHealthNoti(pet_health_record);
            result = true;
        }
        return result;
    }

    @Override
    public boolean deletePetHealthRecord(String petID) {
        List<Pet_health_record> petHealthRecords = recordRepo.findAll();
        boolean result = false;
        for(Pet_health_record petHealthRecord : petHealthRecords){
            if(petHealthRecord.getPetID().equals(petID)){
                recordRepo.delete(petHealthRecord);
                result = true;
            }
        }
        return result;
    }


}
