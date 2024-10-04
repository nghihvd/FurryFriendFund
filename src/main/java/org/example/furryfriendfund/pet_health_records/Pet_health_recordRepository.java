package org.example.furryfriendfund.pet_health_records;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Pet_health_recordRepository extends JpaRepository<Pet_health_record, String> {
    List<Pet_health_record> findPet_health_recordByPetID(String petID);
}
