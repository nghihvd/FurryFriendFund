package org.example.furryfriendfund.accounts;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data // automatic create getter,setter, toString, hashCode
@NoArgsConstructor // create default constructor_ User()
@AllArgsConstructor
public class AccountResponse {
    private String accountID;

    private String password;
    private String name;
    private int roleID;
    private String note;
    private String sex;
    private String birthdate;
    private String address;
    private String phone;
    private double total_donation;
}
