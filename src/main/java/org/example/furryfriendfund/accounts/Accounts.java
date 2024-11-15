package org.example.furryfriendfund.accounts;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Entity
@Data // automatic create getter,setter, toString, hashCode
@NoArgsConstructor // create default constructor_ User()
@AllArgsConstructor // create constructor for all properties_ User(....)
 // ánh xạ thực thể này đến bảng users để tránh trường hợp tự động tạo bảng mới khi

public class Accounts {
    @Id // mark userID  is the PK
    @Column(nullable = false, unique = true) // mark that userID column can not null and unique
    private String accountID;


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
