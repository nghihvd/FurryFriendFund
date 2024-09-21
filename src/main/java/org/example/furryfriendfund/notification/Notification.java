package org.example.furryfriendfund.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="notifications")
public class Notification {
    @Id
    private String notiID ;

    private String accountID;
    private String message;
    private String petID;
    private boolean status;
}
