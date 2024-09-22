package org.example.furryfriendfund.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
