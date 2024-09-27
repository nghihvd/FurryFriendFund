package org.example.furryfriendfund.pets;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IPetsService {
    Pets addPet(Pets pet);
    List<Pets> searchPetsByName(String name);
    List<Pets> searchPetsByCategory(int category);
    String saveImage(MultipartFile file) throws IOException;
}
