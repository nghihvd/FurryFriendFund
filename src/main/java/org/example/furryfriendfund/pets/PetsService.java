package org.example.furryfriendfund.pets;


import org.example.furryfriendfund.events.Events;
import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PetsService implements IPetsService {
    @Autowired
    private PetsRepository petsRepository;
    @Autowired
    private NotificationService notificationService;

    private static final Path CURRENT_FOLDER =
            Paths.get(System.getProperty("user.dir"));

    @Override
    public Pets addPet(Pets pet) {

        Pets petInfo = null;
        if (pet.getStatus() == null || !pet.getStatus().equals("Available")) {
            pet.setStatus("UnAvailable");
            petInfo = petsRepository.save(pet);
            notificationService.createNewPetNotification(pet);
        }
        return petInfo;
    }

    @Override
    public Pets findPetById(String petId) {
        return petsRepository.findById(petId).orElse(null);
    }

    @Override
    public List<Pets> showListAll() {
        return petsRepository.findAll();
    }

    @Override
    public List<Pets> searchPetsByNameAdmin(String name, float age, String sex, int categoryID) {
        List<Pets> petsInName = petsRepository.findByNameIgnoreCaseAndTrimmed(name);

        // Call the new filtering method
        return filterPets(petsInName, age, sex, categoryID);
    }

    @Override
    public Pets savePet(Pets pet) {
        return petsRepository.save(pet);
    }



    @Override
    public boolean deletePet(String petId) {
        boolean result = false;
        petsRepository.deleteById(petId);
        if(findPetById(petId) == null){
            result = true;
        }
        return result;
    }

    @Override
    public List<Pets> searchPetsByName(String name, float age, String sex, int categoryID) {
        List<Pets> petsInName = petsRepository.findByNameIgnoreCaseAndTrimmed(name);

        // Filter pets and include status check
        List<Pets> filteredPets = filterPets(petsInName, age, sex, categoryID);
        return filteredPets.stream()
                .filter(pet -> pet.getStatus().equalsIgnoreCase("Available") || pet.getStatus().equalsIgnoreCase("Waiting"))
                .collect(Collectors.toList());
    }

    // New helper method to filter pets based on criteria
    private List<Pets> filterPets(List<Pets> petsInName, float age, String sex, int categoryID) {
        List<Pets> searchPets = new ArrayList<>();

        for (Pets pet : petsInName) {
            boolean matches = true;

            // Check age
            if (age != 0.0f && pet.getAge() != age) {
                matches = false;
            }

            // Check sex
            if (!sex.equals("") && !pet.getSex().equalsIgnoreCase(sex)) {
                matches = false;
            }

            // Check categoryID
            if (categoryID != 0 && pet.getCategoryID() != categoryID) {
                matches = false;
            }

            // If all conditions are met, add to the result list
            if (matches) {
                searchPets.add(pet);
            }
        }
        return searchPets;
    }


    @Override
    public List<Pets> showList() {
        List<Pets> pets = petsRepository.findAll();
        return pets.stream()
                .filter(pet -> pet.getStatus().equalsIgnoreCase("Available") || pet.getStatus().equalsIgnoreCase("Waiting"))
                .collect(Collectors.toList());
    }

    @Override
    public Pets updatePet(String petID, PetsDTO petsDTO) throws IOException {
        Pets petUpdate = petsRepository.findById(petID).orElse(null);
        if (petUpdate == null) {
            return null;
        }
//        Pets oldPetInfo = new Pets();
//        BeanUtils.copyProperties(petUpdate, oldPetInfo);
//
//        BeanUtils.copyProperties(petsDTO, petUpdate, "img_url");

        if (petsDTO.getImg_url() != null && !petsDTO.getImg_url().isEmpty()) {
            String originalFileName = petsDTO.getImg_url().getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

            if (!allowedExtensions.contains(fileExtension)) {
                throw new IllegalArgumentException("Invalid file format. Only accept file format: .jpg, .jpeg, .png, .gif");
            }

            String newImageFileName = UUID.randomUUID().toString() + "_" + originalFileName;


            String currentFileName = Paths.get(petUpdate.getImg_url()).getFileName().toString();
            if (!newImageFileName.equalsIgnoreCase(currentFileName)) {
                // Đường dẫn tới thư mục lưu trữ file
                Path imagePath = Paths.get("static", "images");

                if (!Files.exists(CURRENT_FOLDER.resolve(imagePath))) {
                    Files.createDirectories(CURRENT_FOLDER.resolve(imagePath));
                }

                // Đường dẫn file mới
                Path file = CURRENT_FOLDER.resolve(imagePath).resolve(newImageFileName);

                // Lưu file ảnh
                try (OutputStream outputStream = Files.newOutputStream(file)) {
                    outputStream.write(petsDTO.getImg_url().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("Can't save file: " + e.getMessage(), e);
                }
                petUpdate.setImg_url(file.toString());
            }
        }



        if (!petsDTO.getName().trim().isEmpty() && !petUpdate.getName().equals(petsDTO.getName()))
        {
            petUpdate.setName(petsDTO.getName());
        }
        if (!petsDTO.getAccoutID().trim().isEmpty()&&!petUpdate.getAccountID().equals(petsDTO.getAccoutID())) {
            petUpdate.setAccountID(petsDTO.getAccoutID());
        }
        if (!petsDTO.getBreed().trim().isEmpty()&&!petUpdate.getBreed().equals(petUpdate.getBreed())){
            petUpdate.setBreed(petsDTO.getBreed());
        }
        if (!petsDTO.getSex().trim().isEmpty() && !petUpdate.getSex().equals(petUpdate.getSex())){
            petUpdate.setSex(petsDTO.getSex());
        }
        if (petsDTO.getAge() == 0 && petsDTO.getAge() != petUpdate.getAge()){
            petUpdate.setAge(petsDTO.getAge());
        }
        if (petsDTO.getWeight() != 0 && petsDTO.getWeight() != petUpdate.getWeight()){
            petUpdate.setWeight(petsDTO.getWeight());
        }
//        if (petsDTO.getStatus = false && p) isUpdated = true;
//        if (!Objects.equals(petUpdate.getNote(), oldPetInfo.getNote())) isUpdated = true;
//        if (!Objects.equals(petUpdate.getSize(), oldPetInfo.getSize())) isUpdated = true;
//        if (petUpdate.isPotty_trained() != oldPetInfo.isPotty_trained()) isUpdated = true;
//        if (petUpdate.isDietary_requirements() != oldPetInfo.isDietary_requirements()) isUpdated = true;
//        if (petUpdate.isSpayed() != oldPetInfo.isSpayed()) isUpdated = true;
//        if (petUpdate.isVaccinated() != oldPetInfo.isVaccinated()) isUpdated = true;
//        if (petUpdate.isSocialized() != oldPetInfo.isSocialized()) isUpdated = true;
//        if (petUpdate.isRabies_vaccinated() != oldPetInfo.isRabies_vaccinated()) isUpdated = true;
//        if (!Objects.equals(petUpdate.getOrigin(), oldPetInfo.getOrigin())) isUpdated = true;
//        if (!Objects.equals(petUpdate.getImg_url(), oldPetInfo.getImg_url())) isUpdated = true;
//        if (!Objects.equals(petUpdate.getCategoryID(), oldPetInfo.getCategoryID())) isUpdated = true;
//        if (!Objects.equals(petUpdate.getDescription(), oldPetInfo.getDescription())) isUpdated = true;
//        if (isUpdated) {
//            petUpdate.setStatus("Updating");
//            Pets savedPet = petsRepository.save(petUpdate);
//            notificationService.createNewPetNotification(savedPet);
//            return savedPet;
//        }
        return null;
    }

}
