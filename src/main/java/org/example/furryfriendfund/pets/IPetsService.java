package org.example.furryfriendfund.pets;

import java.util.List;

public interface IPetsService {
    Pets addPet(Pets pet);
    List<Pets> searchPetsByName(String name);
    List<Pets> searchPetsByBreed(String breed);
    List<Pets> showList();
    List<Pets> showPetsById(String petId);
    Pets findPetById(String petId);
}
