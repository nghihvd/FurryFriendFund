package org.example.furryfriendfund.pets;

import java.util.List;

public interface IPetsService {
    Pets addPet(Pets pet);
    List<Pets> searchPetsByName(String name);
    List<Pets> searchPetsByCategory(int category);
}
