package org.example.furryfriendfund.pets;

import java.util.List;

public interface IPetsService {
    Pets addPet(Pets pet);
    List<Pets> searchPetsByName(String name,float age,String sex, int categoryID);
    List<Pets> showList();
    Pets findPetById(String petId);
    List<Pets> showListAll();
    List<Pets> searchPetsByNameAdmin(String name,float age,String sex, int categoryID);
}
