package org.example.furryfriendfund.pets;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PetsDTO {



    private String name;

    private String breed;
    private String sex;
    private float age;
    private float weight;
    private String status;
    private String note;
    private String size;
    private boolean potty_trained; // đi vệ sinh đúng chỗ
    private boolean dietary_requirements; // chế độ ăn riêng
    private boolean spayed; // triệt sản
    private boolean vaccinated; // chích ngừa
    private boolean socialized; // thân thiện
    private boolean rabies_vaccinated; // tiêm dại
    private String origin;
    private MultipartFile img_url;
    @Setter
    private int categoryID;
    private String description;
    private String accoutID;

}
