package org.example.furryfriendfund.accounts;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private String accountID;

    @Nullable
    private String password;
    private String name;
    private int roleID;
    private String note;
    private String sex;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @Temporal(TemporalType.DATE)
    private Date birthdate;
    private String address;
    private String phone;
    private double total_donation;
    private boolean married;
    private String job;
    private int income;
    private String citizen_serial;
    private boolean experience_caring;
    private String confirm_address;
    private String email;
}
