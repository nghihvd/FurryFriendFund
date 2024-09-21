package org.example.furryfriendfund.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notiID;

    private String accountID;
    private String message;
    private String eventID;
    private String petID;
    private String appointID;
    private boolean status;
}
