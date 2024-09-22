package org.example.furryfriendfund.pets;

import java.util.List;

public interface IPetsService {
    Pets addPet(Pets pet);
    List<Pets> searchPets(String name, int categoryID);
}
