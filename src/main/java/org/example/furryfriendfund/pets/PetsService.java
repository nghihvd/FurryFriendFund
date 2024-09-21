package org.example.furryfriendfund.pets;


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
    public List<Pets> searchPets(String name, int categoryID) {
        return List.of();
    }
}
