package org.example.furryfriendfund.pets;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="pets")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pets {

    @Id
    private String petID;

    private String name;
    private String accountID;
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
    private String img_url;
    private int categoryID;
    private LocalDateTime adopt_date;
    private String description;
//    private LocalDateTime date_time_report;
//
//    @Lob
//    @Column(columnDefinition = "MEDIUMBLOB")
//    private byte[] video_report;

}
