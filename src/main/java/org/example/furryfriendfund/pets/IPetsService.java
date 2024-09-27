package org.example.furryfriendfund.pets;

import java.util.List;

public interface IPetsService {
    Pets addPet(Pets pet);
    List<Pets> searchPetsByNameAndBreed(String name,float age,String sex, int categoryID,String breed);
    List<Pets> showList();
    List<Pets> showPetsById(String petId);
    Pets findPetById(String petId);
}
