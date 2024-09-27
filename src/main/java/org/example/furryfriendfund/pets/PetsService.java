package org.example.furryfriendfund.pets;


import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service

public class PetsService implements IPetsService {
    @Autowired
    private PetsRepository petsRepository;

    @Override
    public Pets addPet(Pets pet) {

        return petsRepository.save(pet);
    }


    @Override
    public List<Pets> searchPetsByName(String name) {
        return petsRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<Pets> searchPetsByCategory(int category) {
        return petsRepository.findByCategoryID(category);
    }

    @Override
    public String saveImage(MultipartFile file) throws IOException {
        // tạo đường dẫn duy nhất
        String fileName = file.getOriginalFilename(); // lấy tên file
        String extension = fileName.substring(fileName.lastIndexOf(".")); // lấy extensiom
        String generatedFileName = UUID.randomUUID().toString() + extension; // đổi tên file thành duy nhất
        // xác định đường dẫn lưu file
        Path uploadImg  = Paths.get("D:/SE/Se_5/FurryFriendFund/FurryFriendFund");
        // lưu file vào vị trí mà mình đã xác định
        file.transferTo(uploadImg);
        String baseURL = "http://fundfe.vercel.app/image/"; // đường dânx để thấy hình ảnh
        return baseURL+generatedFileName;
    }

}
