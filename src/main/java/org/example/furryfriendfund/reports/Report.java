package org.example.furryfriendfund.reports;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="reports")
public class Report {
    @Id
    private String reportID;

    private String petID;
    private LocalDateTime date_report;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] video;
}
