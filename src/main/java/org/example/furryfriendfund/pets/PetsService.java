package org.example.furryfriendfund.pets;


import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service

public class PetsService implements IPetsService {
    @Autowired
    private PetsRepository petsRepository;
    @Autowired
    private NotificationService notificationService;

    @Override
    public Pets addPet(Pets pet) {

        Pets petInfo = null;
        if (pet.getStatus() == null || !pet.getStatus().equals("Available")) {
            pet.setStatus("UnAvailable");
            petInfo = petsRepository.save(pet);
            notificationService.createNewPetNotification(pet);
        }
        return petInfo;
    }

    @Override
    public Pets findPetById(String petId) {
        return petsRepository.findById(petId).orElse(null);
    }

    @Override
    public List<Pets> showListAll() {
        return petsRepository.findAll();
    }

    @Override
    public List<Pets> searchPetsByNameAdmin(String name, float age, String sex, int categoryID) {
        List<Pets> petsInName = petsRepository.findByNameIgnoreCaseAndTrimmed(name);

        // Call the new filtering method
        return filterPets(petsInName, age, sex, categoryID);
    }

    @Override
    public Pets savePet(Pets pet) {
        return petsRepository.save(pet);
    }

    @Override
    public boolean deletePet(String petId) {
        boolean result = false;
        petsRepository.deleteById(petId);
        if(findPetById(petId) == null){
            result = true;
        }
        return result;
    }

    @Override
    public List<Pets> searchPetsByName(String name, float age, String sex, int categoryID) {
        List<Pets> petsInName = petsRepository.findByNameIgnoreCaseAndTrimmed(name);

        // Filter pets and include status check
        List<Pets> filteredPets = filterPets(petsInName, age, sex, categoryID);
        return filteredPets.stream()
                .filter(pet -> pet.getStatus().equalsIgnoreCase("Available") || pet.getStatus().equalsIgnoreCase("Waiting"))
                .collect(Collectors.toList());
    }

    // New helper method to filter pets based on criteria
    private List<Pets> filterPets(List<Pets> petsInName, float age, String sex, int categoryID) {
        List<Pets> searchPets = new ArrayList<>();

        for (Pets pet : petsInName) {
            boolean matches = true;

            // Check age
            if (age != 0.0f && pet.getAge() != age) {
                matches = false;
            }

            // Check sex
            if (!sex.equals("") && !pet.getSex().equalsIgnoreCase(sex)) {
                matches = false;
            }

            // Check categoryID
            if (categoryID != 0 && pet.getCategoryID() != categoryID) {
                matches = false;
            }

            // If all conditions are met, add to the result list
            if (matches) {
                searchPets.add(pet);
            }
        }
        return searchPets;
    }


    @Override
    public List<Pets> showList() {
        List<Pets> pets = petsRepository.findAll();
        return pets.stream()
                .filter(pet -> pet.getStatus().equalsIgnoreCase("Available") || pet.getStatus().equalsIgnoreCase("Waiting"))
                .collect(Collectors.toList());
    }


}
