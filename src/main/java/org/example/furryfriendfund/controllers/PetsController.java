package org.example.furryfriendfund.controllers;


import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationService;
import org.example.furryfriendfund.pets.Pets;
import org.example.furryfriendfund.pets.PetsDTO;
import org.example.furryfriendfund.pets.PetsRepository;
import org.example.furryfriendfund.pets.PetsService;

import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/pets")
public class PetsController {

    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));
    @Autowired
    private PetsService petsService;
    @Autowired
    private PetsRepository petsRepository;
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/addPets")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<?> addPet(@ModelAttribute PetsDTO petsDTO) throws IOException {

        Path staticPath = Paths.get("static");
        Path imagePath = Paths.get("images");
        if (!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
        }
        Path file = CURRENT_FOLDER.resolve(staticPath).resolve(imagePath)
                .resolve(Objects.requireNonNull(petsDTO.getImg_url().getOriginalFilename()));

        try (OutputStream outputStream = Files.newOutputStream(file)) {
            outputStream.write(petsDTO.getImg_url().getBytes());
        }
        Pets pet = new Pets();
        pet.setPetID(UUID.randomUUID().toString().substring(0, 8));
        BeanUtils.copyProperties(petsDTO, pet, "img_url", "petID", "categoryID");// not copy img_url
        pet.setImg_url(imagePath.resolve(petsDTO.getImg_url().getOriginalFilename()).toString());
        pet.setCategoryID(petsDTO.getCategoryID());
        Pets petInfo = petsService.addPet(pet);
        if (petInfo == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(petInfo);

    }

    /**
     * hàm tìm kiếm pet cho member || guest
     * có data là name, breed, age, categoryID, sex
     * 
     * @return danh sách các pet được tìm kiếm = name || breed. Còn
     *         age,sex,categoryID thiếu cũng đc
     *
     */

    @GetMapping("/searchByNameAndBreed")
    public ResponseEntity<?> searchByNameAndBreed(
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("message", "Error occurred")
                    .body(null);
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
        if (!foundPet.isEmpty()) {
            return ResponseEntity.ok(foundPet);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/showListAllOfPets")
    @PreAuthorize("hasAuthority('2') or hasAuthority('1')")
    public ResponseEntity<List<Pets>> showListAllOfPets() {
        List<Pets> foundPet = petsService.showListAll();
        if (foundPet != null) {
            return ResponseEntity.ok(foundPet);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/searchByNameAndBreedAdmin")
    @PreAuthorize("hasAuthority('2') or hasAuthority('1')")
    public ResponseEntity<?> searchByNameAndBreedAdmin(
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("message", "Error occurred")
                    .body(null);
        }
        return ResponseEntity.ok().header("message", "Found pets").body(foundPets);
    }

    @GetMapping("/getByID/{petID}")
    public ResponseEntity<BaseResponse> getByID(@PathVariable String petID) {
        ResponseEntity<BaseResponse> response;
        Pets pets = petsService.findPetById(petID);
        if (pets != null) {
            response = ResponseUtils.createSuccessRespone("Pet found", pets);
        } else {
            response = ResponseUtils.createErrorRespone("No Pet found", null, HttpStatus.NOT_FOUND);
        }
        return response;
    }


    @PostMapping("/{petID}/updatePets")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> updatePets(@PathVariable String petID, @ModelAttribute PetsDTO petsDTO)
            throws IOException {
        // Gọi tầng service để cập nhật sự kiện
        Pets updatePet = petsService.updatePet(petID, petsDTO);
        // Trả về kết quả
        return updatePet != null
                ? ResponseUtils.createSuccessRespone("Update successfully", updatePet)
                : ResponseUtils.createErrorRespone("Update failed", null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/historyAdopt/{accountID}")
    @PreAuthorize("hasAuthority('3')")
    public ResponseEntity<BaseResponse> historyAdopt(@PathVariable String accountID) {
        ResponseEntity<BaseResponse> response;
        try {
            List<Pets> historyAdopt = petsService.getByAccountID(accountID);
            if (historyAdopt.isEmpty()) {
                response = ResponseUtils.createErrorRespone("No Pet found", null, HttpStatus.NOT_FOUND);
            } else {
                response = ResponseUtils.createSuccessRespone("Pet found", historyAdopt);
            }
        } catch (Exception e) {
            response = ResponseUtils.createErrorRespone(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PutMapping("/returnPets/{petID}")
    @PreAuthorize("hasAuthority('3')")
    public ResponseEntity<BaseResponse> returnPets(
            @PathVariable String petID,
            @RequestParam("reason") String reason) {
        Pets petToReturn = petsService.findPetById(petID);
        boolean checkTrust = notificationService.checkExistTrustRequest(petID);
        if(checkTrust){
            return ResponseUtils.createErrorRespone("Trust request have been already sent. Cannot return pet", null, HttpStatus.CONFLICT);
        }
        petToReturn.setStatus("Processing");
        petsService.savePet(petToReturn);

        Notification notification = notificationService.createNotificationReturnPetRequest(petID, petToReturn.getAccountID(), reason);
        return ResponseUtils.createSuccessRespone("Return request sent. Waiting for staff confirmation.", notification);
    }

    @PutMapping("/notReturnPets/{petID}")
    @PreAuthorize("hasAuthority('3')")
    public ResponseEntity<BaseResponse> notReturnPets(@PathVariable String petID) {
        Pets petToNotReturn = petsService.findPetById(petID);
        if(petToNotReturn.getNote().equalsIgnoreCase("Processing")) {
           petToNotReturn.setStatus("Adopted");
           petsService.savePet(petToNotReturn);
           Notification noti = notificationService.findByMessageReturnPet(petID);
           if(noti != null) {
               notificationService.deleteNoti(noti.getNotiID());
               return ResponseUtils.createSuccessRespone("Your pet's return request has been canceled.", noti);
           }
        }
        return ResponseUtils.createErrorRespone("No request return pet found", null, HttpStatus.NOT_FOUND);
    }

}
