package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pets")
public class PetsController {

    @Autowired
    private PetsService petsService;

    @PostMapping("/addPets")
    public ResponseEntity<Pets> addPet(@RequestBody Pets pet) {
        pet.setPetID(UUID.randomUUID().toString().substring(0,8));
        Pets newPet = petsService.addPet(pet);
        return ResponseEntity.created(URI.create("/pets/addPets")).body(newPet);
    }
    @PostMapping("/searchByName")
    public ResponseEntity<List<Pets>> searchByName(@RequestBody Pets pet)  {
        String name = pet.getName();
        List<Pets> foundPet = petsService.searchPetsByName(name);
        if (foundPet != null ) {
            return ResponseEntity.ok().header("message","found pets").body(foundPet);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/searchByBreed")
    public ResponseEntity<List<Pets>> searchByBreed(@RequestBody Pets pet) {
        String breed = pet.getBreed();

        List<Pets> foundPet = petsService.searchPetsByBreed(breed);
        if (foundPet != null) {
            return ResponseEntity.ok().header("message","found pets").body(foundPet);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/showListOfPets")
    public ResponseEntity<List<Pets>> showListOfPets() {
        List<Pets> foundPet = petsService.showList();
        if(foundPet != null) {
            return ResponseEntity.ok(foundPet);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


}
