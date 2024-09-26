package org.example.furryfriendfund.pets;


import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public List<Pets> showPetsById(String petId){
        return petsRepository.findAllById(Arrays.asList(petId.split(",")));
    }

    @Override
    public Pets findPetById(String petId) {
        return petsRepository.findById(petId).orElse(null);
    }

    @Override
    public List<Pets> searchPetsByName(String name) {
        List<Pets> petsInName = petsRepository.findByNameIgnoreCase(name);

        for(Pets pet : petsInName) {

        }


        return petsInName.stream()
                .filter(pet -> pet.getStatus().equals("Available") || pet.getStatus().equals("Waiting"))
                .collect(Collectors.toList());
    }


    @Override
    public List<Pets> searchPetsByBreed(String breed) {
        // Lấy tất cả các pets theo categoryID
        List<Pets> petsInBreed = petsRepository.findByBreed(breed);

        // Lọc các thú cưng có status là "Available" hoặc "Waiting"
        return petsInBreed.stream()
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
