package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsDTO;
import org.example.furryfriendfund.pets.PetsService;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> addPet(@ModelAttribute PetsDTO petsDTO) throws IOException {
        Path staticPath = Paths.get("static");
        Path imagePath = Paths.get("images");
        if(!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))){
            Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
        }
        Path  file = CURRENT_FOLDER.resolve(staticPath).resolve(imagePath)
                .resolve(Objects.requireNonNull(petsDTO.getImg_url().getOriginalFilename()));
        try(OutputStream outputStream = Files.newOutputStream(file)){
            outputStream.write(petsDTO.getImg_url().getBytes());
        }
        Pets pet = new Pets();
        pet.setPetID(UUID.randomUUID().toString().substring(0, 8));
        BeanUtils.copyProperties(petsDTO, pet,"img_url","petID");// not copy img_url
        pet.setImg_url(imagePath.resolve(petsDTO.getImg_url().getOriginalFilename()).toString());
        Pets petInfo = petsService.addPet(pet);
        if(petInfo == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(petInfo);

    }

    /**
     * hàm tìm kiếm pet cho member || guest
     * có data là name, breed, age, categoryID, sex
     * @return danh sách các pet được tìm kiếm = name || breed. Còn age,sex,categoryID thiếu cũng đc
     *
     */

    @GetMapping ("/searchByNameAndBreed")
    public ResponseEntity<?> searchByNameAndBreed( @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                   @RequestParam(value = "age", required = false, defaultValue = "0.0") float age,
                                                   @RequestParam(value = "categoryID", required = false, defaultValue = "0") int categoryID,
                                                   @RequestParam(value = "sex", required = false, defaultValue = "") String sex) {


        List<Pets> foundPets;

        try {
            foundPets = petsService.searchPetsByName(name, age, sex, categoryID);
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
     * return 1 danh sách
     */

    @GetMapping("/showListOfPets")
    public ResponseEntity<List<Pets>> showListOfPets() {
        List<Pets> foundPet = petsService.showList();
        if (foundPet != null) {
            return ResponseEntity.ok(foundPet);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @GetMapping("/showListAllOfPets")
    public ResponseEntity<List<Pets>> showListAllOfPets()
    {
        List<Pets> foundPet = petsService.showListAll();
        if (foundPet != null) {
            return ResponseEntity.ok(foundPet);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @GetMapping ("/searchByNameAndBreedAdmin")
    public ResponseEntity<?> searchByNameAndBreedAdmin(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                       @RequestParam(value = "age", required = false, defaultValue = "0.0") float age,
                                                       @RequestParam(value = "categoryID", required = false, defaultValue = "0") int categoryID,
                                                       @RequestParam(value = "sex", required = false, defaultValue = "") String sex) {
        List<Pets> foundPets;

        try {
            foundPets = petsService.searchPetsByNameAdmin(name, age, sex, categoryID);
            if (foundPets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header("message", "No pets found").body(foundPets);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("message", "Error occurred").body(null);
        }

        return ResponseEntity.ok().header("message", "Found pets").body(foundPets);
    }

}
