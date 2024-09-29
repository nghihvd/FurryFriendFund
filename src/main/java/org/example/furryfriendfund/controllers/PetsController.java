package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
                                    @RequestParam("description") String description) throws IOException {
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

    /**
     * hàm tìm kiếm pet cho member || guest
     * @param pet có data là name, breed, age, categoryID, sex
     * @return danh sách các pet được tìm kiếm = name || breed. Còn age,sex,categoryID thiếu cũng đc
     *
     */

    @GetMapping ("/searchByNameAndBreed")
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


    @GetMapping ("/searchByNameAndBreed")
    public ResponseEntity<?> searchByNameAndBreedAdmin(@RequestBody Pets pet) {
        String name = pet.getName() != null ? pet.getName() : ""; // Nếu name null, đặt mặc định là chuỗi rỗng
        String breed = pet.getBreed() != null ? pet.getBreed() : ""; // Nếu breed null, đặt mặc định là chuỗi rỗng
        float age = pet.getAge() != 0.0f ? pet.getAge() : 0.0f; // Nếu age không được cung cấp, đặt mặc định -1
        int categoryID = pet.getCategoryID() != 0 ? pet.getCategoryID() : 0; // Nếu categoryID không được cung cấp, đặt mặc định -1
        String sex = pet.getSex() != null ? pet.getSex() : ""; // Giữ sex là null nếu không có giá trị

        List<Pets> foundPets;

        try {
            foundPets = petsService.searchPetsByNameAndBreedAdmin(name, age, sex, categoryID, breed);
            if (foundPets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header("message", "No pets found").body(foundPets);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("message", "Error occurred").body(null);
        }

        return ResponseEntity.ok().header("message", "Found pets").body(foundPets);
    }

}
