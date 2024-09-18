package org.example.furryfriendfund.pets;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="pets")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Pets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tự
    private String petID;

    private String name;
    private String userID;
    private String breed;
    private String sex;
    private int age;
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
    private String img_url;
}
