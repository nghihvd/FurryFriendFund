package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pets")
public class PetsController {

    private static final Logger log = LoggerFactory.getLogger(PetsController.class);
    @Autowired
    private PetsService petsService;

    @PostMapping("/addPets")
    public ResponseEntity<Pets> addPet(@RequestBody Pets pet) {
        pet.setPetID(UUID.randomUUID().toString().substring(0, 8));
        Pets newPet = petsService.addPet(pet);
        return ResponseEntity.created(URI.create("/pets/addPets")).body(newPet);
    }

    /**
     * hàm tìm kiếm pet cho member || guest
     * @param pet có data là name, breed, age, categoryID, sex
     * @return danh sách các pet được tìm kiếm = name || breed. Còn age,sex,categoryID thiếu cũng đc
     *
     */

    @PostMapping("/searchByNameAndBreed")
    public ResponseEntity<?> searchByNameAndBreed(@RequestBody Pets pet) {
        String name = pet.getName() != null ? pet.getName() : ""; // Nếu name null, đặt mặc định là chuỗi rỗng
        String breed = pet.getBreed() != null ? pet.getBreed() : ""; // Nếu breed null, đặt mặc định là chuỗi rỗng
        float age = pet.getAge() != 0.0f ? pet.getAge() : 0.0f; // Nếu age không được cung cấp, đặt mặc định -1
        int categoryID = pet.getCategoryID() != 0 ? pet.getCategoryID() : 0; // Nếu categoryID không được cung cấp, đặt mặc định -1
        String sex = pet.getSex() != null ? pet.getSex() : ""; // Giữ sex là null nếu không có giá trị

        List<Pets> foundPets;

        try {
            foundPets = petsService.searchPetsByNameAndBreed(name, age, sex, categoryID, breed);
            if (foundPets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header("message", "No pets found").body(foundPets);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("message", "Error occurred").body(null);
        }

        return ResponseEntity.ok().header("message", "Found pets").body(foundPets);
    }


    /**
     * hàm để hiển thị danh sách của thú cưng cho member || guest
     * @return
     */

    @PostMapping("/showListOfPets")
    public ResponseEntity<List<Pets>> showListOfPets() {
        List<Pets> foundPet = petsService.showList();
        if (foundPet != null) {
            return ResponseEntity.ok(foundPet);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


}
