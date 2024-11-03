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

    /**
     *
     * @param name 
     * @param age
     * @param sex
     * @param categoryID
     * @return
     */
    @Override
    public List<Pets> searchPetsByName(String name, float age, String sex, int categoryID) {
        List<Pets> petsInName = petsRepository.findByNameIgnoreCaseAndTrimmed(name);

        // Filter pets and include status check
        List<Pets> filteredPets = filterPets(petsInName, age, sex, categoryID);
        return filteredPets.stream()
                .filter(pet -> pet.getStatus().equalsIgnoreCase("Available") || pet.getStatus().equalsIgnoreCase("Waiting"))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param petsInName
     * @param age
     * @param sex
     * @param categoryID
     * @return
     */


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

    /**
     *
     * @return
     */
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
            Path staticPath = Paths.get("static");
            Path imagePath = Paths.get("images");
            if(!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))){
                Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
            }
            String originalFileName = petsDTO.getImg_url().getOriginalFilename();
            String newName = UUID.randomUUID() +"_"+originalFileName;
            Path  file = CURRENT_FOLDER.resolve(staticPath).resolve(imagePath)
                    .resolve(Objects.requireNonNull(newName));
            try(OutputStream outputStream = Files.newOutputStream(file)){
                outputStream.write(petsDTO.getImg_url().getBytes());
            }
            petUpdate.setImg_url(imagePath.resolve(newName).toString());
        }


        if (petsDTO.getName() != null ){
            if (!petsDTO.getName().trim().isEmpty() && !petUpdate.getName().equals(petsDTO.getName()))
            {
                petUpdate.setName(petsDTO.getName());
            }
        }

        if(petsDTO.getAccoutID() != null){
            if (!petsDTO.getAccoutID().trim().isEmpty()&&!petUpdate.getAccountID().equals(petsDTO.getAccoutID())) {
                petUpdate.setAccountID(petsDTO.getAccoutID());
            }
        }
        if (petsDTO.getBreed() != null){
            if (!petsDTO.getBreed().trim().isEmpty()&&!petUpdate.getBreed().equals(petsDTO.getBreed())){
                petUpdate.setBreed(petsDTO.getBreed());
            }
        }
        if(petsDTO.getSex() != null){
            if (!petsDTO.getSex().trim().isEmpty() && !petUpdate.getSex().equals(petsDTO.getSex())){
                petUpdate.setSex(petsDTO.getSex());
            }
        }
        if (petsDTO.getAge() != 0 && petsDTO.getAge() != petUpdate.getAge()){
            petUpdate.setAge(petsDTO.getAge());
        }
        if (petsDTO.getWeight() != 0 && petsDTO.getWeight() != petUpdate.getWeight()){
            petUpdate.setWeight(petsDTO.getWeight());
        }
        if (petsDTO.getStatus() != null){
            if (!petsDTO.getStatus().trim().isEmpty() && !petsDTO.getStatus().equals(petUpdate.getStatus())) {
                petUpdate.setStatus(petsDTO.getStatus());
            }
        }
        if (petsDTO.getNote() != null){
            if (!petsDTO.getNote().trim().isEmpty() && !petsDTO.getNote().equals(petUpdate.getNote())){
                petUpdate.setNote(petsDTO.getNote());
            }
        }
        if (petsDTO.getSize() != null){
            if (!petsDTO.getSize().trim().isEmpty() && !petsDTO.getSize().equals(petUpdate.getSize())){
                petUpdate.setSize(petsDTO.getSize());
            }
        }

        if (petsDTO.isPotty_trained() != petUpdate.isPotty_trained()) {
            petUpdate.setPotty_trained(petsDTO.isPotty_trained());
        }
        if (petUpdate.isDietary_requirements() != petsDTO.isDietary_requirements()){
            petUpdate.setDietary_requirements(petsDTO.isDietary_requirements());
        }
        if (petUpdate.isSpayed() != petsDTO.isSpayed()) {
            petUpdate.setSpayed(petsDTO.isSpayed());
        }
        if (petUpdate.isVaccinated() != petsDTO.isVaccinated()) {
            petUpdate.setVaccinated(petsDTO.isVaccinated());
        }
        if (petUpdate.isSocialized() != petsDTO.isSocialized()){
            petUpdate.setSocialized(petsDTO.isSocialized());
        }
        if (petUpdate.isRabies_vaccinated() != petsDTO.isRabies_vaccinated()) {
            petUpdate.setRabies_vaccinated(petsDTO.isRabies_vaccinated());
        }
        if (petsDTO.getOrigin() != null){
            if (!petUpdate.getOrigin().trim().isEmpty() && !petUpdate.getOrigin().equals(petsDTO.getOrigin())){
                petUpdate.setOrigin(petsDTO.getOrigin());
            }
        }
        if (petsDTO.getCategoryID() != 0){
            if (petUpdate.getCategoryID() !=  petsDTO.getCategoryID()){
                petUpdate.setCategoryID(petsDTO.getCategoryID());
            }
        }
        if (petsDTO.getDescription() != null){
            if (!petUpdate.getDescription().trim().isEmpty() && petUpdate.getDescription() != petsDTO.getDescription()){
                petUpdate.setDescription(petsDTO.getDescription());
            }
        };
        Pets savedPet = petsRepository.save(petUpdate);
        notificationService.updatePetNoti(petUpdate);
        return savedPet;

    }

    @Override
    public List<Pets> getByAccountID(String accountID) {
        return petsRepository.getPetsByAccountID(accountID);
    }

    @Override
    public boolean deletePetById(String petId) {
        boolean result = false;
        Pets search = petsRepository.getPetByPetID(petId);
        if(search.getAccountID() == null){
            petsRepository.delete(search);
            result = true;
        }
        return result;
    }


}
