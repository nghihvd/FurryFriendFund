package org.example.furryfriendfund.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="notifications",uniqueConstraints = @UniqueConstraint(columnNames = "notiID"))
public class Notification {
    @Id
    private String notiID ;

    private String accountID;
    private String message;
    private String petID;
    private int roleID;
    private boolean status;
    private  boolean button_status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

}