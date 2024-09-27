package org.example.furryfriendfund.pets;


import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service

public class PetsService implements IPetsService {
    @Autowired
    private PetsRepository petsRepository;

    @Override
    public Pets addPet(Pets pet) {
        return petsRepository.save(pet);
    }



    @Override
    public Pets findPetById(String petId) {
        return petsRepository.findById(petId).orElse(null);
    }

    @Override
    public List<Pets> searchPetsByNameAndBreed(String name, float age, String sex, int categoryID, String breed) {
        List<Pets> petsInName = petsRepository.findByNameIgnoreCase(name);
        List<Pets> petsInBreed = petsRepository.findByBreedIgnoreCase(breed);

        // Hợp nhất hai danh sách và loại bỏ trùng lặp

        Set<Pets> uniquePets = new HashSet<>(petsInName);
        uniquePets.addAll(petsInBreed);

        List<Pets> searchPets = new ArrayList<>();

        boolean matches = true;

        for (Pets pet : uniquePets) {


            // Kiểm tra tuổi
            if (age == 0.0f && pet.getAge() != age) {
                matches = false;
            }

            // Kiểm tra giới tính
            if (sex == "" && !pet.getSex().equalsIgnoreCase(sex)) {
                matches = false;
            }

            // Kiểm tra categoryID
            if (categoryID == 0 && pet.getCategoryID() != categoryID) {
                matches = false;
            }

            // Nếu tất cả điều kiện thỏa mãn, thêm vào danh sách kết quả
            if (matches) {
                searchPets.add(pet);
            }
        }
        searchPets.addAll(uniquePets);

        return searchPets.stream()
                .filter(pet -> pet.getStatus().equals("Available") || pet.getStatus().equals("Waiting"))
                .collect(Collectors.toList());
    }



    @Override
    public List<Pets> showList() {
        List<Pets> pets = petsRepository.findAll();
        return pets.stream()
                .filter(pet -> pet.getStatus().equals("Available") || pet.getStatus().equals("Waiting"))
                .collect(Collectors.toList());
    }


}
