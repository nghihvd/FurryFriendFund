package org.example.furryfriendfund.pet_health_records;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Data
@Table(name="pet_health_records")
@NoArgsConstructor
@AllArgsConstructor
public class Pet_health_record {
    @Id
    private String recordID;

    private Date check_out_date ;
    private Date check_in_date;
    private String veterinarian_name; // vet's name
    private float veterinary_fee;
    private String illness_name;
    private String note ;
    private String  petID ;
}
