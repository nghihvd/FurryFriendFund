package org.example.furryfriendfund.pets;

import java.io.IOException;
import java.util.List;

public interface IPetsService {
    Pets addPet(Pets pet);
    List<Pets> searchPetsByName(String name,float age,String sex, int categoryID);
    List<Pets> showList();
    Pets findPetById(String petId);
    List<Pets> showListAll();
    List<Pets> searchPetsByNameAdmin(String name,float age,String sex, int categoryID);
    boolean deletePet(String petId);
    Pets savePet(Pets pet);
    Pets updatePet(String petID, PetsDTO petsDTO) throws IOException;
    List<Pets> getByAccountID(String accountID);
    boolean deletePetById(String petId);
    Pets getPetByAppointmentID(String appointmentID);
    int countPet(String status);
}
