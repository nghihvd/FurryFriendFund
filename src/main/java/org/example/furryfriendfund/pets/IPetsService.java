package org.example.furryfriendfund.pets;

import java.util.List;

public interface IPetsService {
    Pets addPet(Pets pet);
    List<Pets> searchPetsByNameAndBreed(String name,float age,String sex, int categoryID,String breed);
    List<Pets> showList();
    Pets findPetById(String petId);
    List<Pets> showListAll();
    List<Pets> searchPetsByNameAndBreedAdmin(String name,float age,String sex, int categoryID,String breed);
}
