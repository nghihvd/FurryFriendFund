package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/pets")
public class PetsController {
    private static final Path CURRENT_FOLDER =
            Paths.get(System.getProperty("user.dir"));
    @Autowired
    private PetsService petsService;

    @PostMapping("/addPets")
    public ResponseEntity<?> addPet(@RequestParam("name") String name,
                                    @RequestParam("breed") String breed,
                                    @RequestParam("sex") String sex,
                                    @RequestParam("age") float age,
                                    @RequestParam("weight") float weight,
                                    @RequestParam("note") String note,
                                    @RequestParam("size") String size,
                                    @RequestParam("potty_trained") boolean potty_trained,
                                    @RequestParam("dietary_requirements") boolean dietary_requirements,
                                    @RequestParam("spayed") boolean spayed,
                                    @RequestParam("vaccinated") boolean vaccinated,
                                    @RequestParam("socialized") boolean socialized,
                                    @RequestParam("rabies_vaccinated") boolean rabies_vaccinated,
                                    @RequestParam("origin") String origin,
                                    final @RequestParam("img_url") MultipartFile img_url,
                                    @RequestParam("categoryID") int categoryID,
                                    @RequestParam("description") String description) throws IOException  {

            Path staticPath = Paths.get("static");
            Path imagePath = Paths.get("images");
            if(!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))){
                Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
            }
            Path  file = CURRENT_FOLDER.resolve(staticPath).resolve(imagePath)
                    .resolve(Objects.requireNonNull(img_url.getOriginalFilename()));
            try(OutputStream outputStream = Files.newOutputStream(file)){
                outputStream.write(img_url.getBytes());
            }
            Pets pet = new Pets();
            pet.setPetID(UUID.randomUUID().toString().substring(0, 8));
            pet.setName(name);
            pet.setBreed(breed);
            pet.setSex(sex);
            pet.setAge(age);
            pet.setWeight(weight);
            pet.setNote(note);
            pet.setSize(size);
            pet.setPotty_trained(potty_trained);
            pet.setDietary_requirements(dietary_requirements);
            pet.setSocialized(socialized);
            pet.setRabies_vaccinated(rabies_vaccinated);
            pet.setOrigin(origin);
            pet.setPotty_trained(potty_trained);
            pet.setSpayed(spayed);
            pet.setVaccinated(vaccinated);
            pet.setCategoryID(categoryID);
            pet.setDescription(description);
            pet.setImg_url(imagePath.resolve(img_url.getOriginalFilename()).toString());
            return ResponseEntity.ok(petsService.addPet(pet));

    }
    @PostMapping("/searchByName")
    public ResponseEntity<List<Pets>> searchByName(@RequestBody Pets pet) {
        String name = pet.getName();
        List<Pets> foundPet = petsService.searchPetsByName(name);
        if (foundPet != null) {
            return ResponseEntity.ok().header("message","found pets").body(foundPet);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/searchByCategory")
    public ResponseEntity<List<Pets>> searchByCategory(@RequestBody Pets pet) {
        int categoryID = pet.getCategoryID();
        List<Pets> foundPet = petsService.searchPetsByCategory(categoryID);
        if (foundPet != null) {
            return ResponseEntity.ok().header("message","found pets").body(foundPet);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
