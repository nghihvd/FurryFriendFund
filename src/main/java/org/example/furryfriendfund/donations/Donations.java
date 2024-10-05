package org.example.furryfriendfund.donations;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="donations",uniqueConstraints = @UniqueConstraint(columnNames = "donateID"))
public class Donations {
    @Id
    private String donateID;

    private String accountID;
    private String eventID;
    private double amount;
    private LocalDateTime date_time;
    private String note;
}
