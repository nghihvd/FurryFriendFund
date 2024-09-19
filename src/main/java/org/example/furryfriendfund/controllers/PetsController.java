package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/pets")
public class PetsController {

    @Autowired
    private PetsService petsService;

    @PostMapping("/addPets")
    public ResponseEntity<Pets> addPet(@RequestBody Pets pet) {
        Pets newPet = petsService.addPet(pet);
        return ResponseEntity.created(URI.create("/pets/addPets")).body(newPet);
    }
}
