package org.example.furryfriendfund.pets;


import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class PetsService implements IPetsService {
    @Autowired
    private PetsRepository petsRepository;

    @Override
    public Pets addPet(Pets pet) {
        return petsRepository.save(pet);
    }

    @Override
    public List<Pets> searchPetsByName(String name) {
        return petsRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<Pets> searchPetsByCategory(int category) {
        return petsRepository.findByCategoryID(category);
    }

}
