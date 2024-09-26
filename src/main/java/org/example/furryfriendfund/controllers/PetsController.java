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
