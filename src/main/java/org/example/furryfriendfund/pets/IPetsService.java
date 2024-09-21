package org.example.furryfriendfund.pets;

import java.util.List;

public interface IPetsService {
    Pets addPet(Pets pet);
    Pets getPetById(String id);
    List<Pets> searchPets(String name, String type);
}
