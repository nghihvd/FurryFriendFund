package org.example.furryfriendfund.accounts;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Entity
@Data // automatic create getter,setter, toString, hashCode
@NoArgsConstructor // create default constructor_ User()
@AllArgsConstructor // create constructor for all properties_ User(....)
@Table (name="accounts",uniqueConstraints = @UniqueConstraint(columnNames = "accountID")) // ánh xạ thực thể này đến bảng users để tránh trường hợp tự động tạo bảng mới khi
//không tự động nhận diện được bảng
public class Accounts {
    @Id // mark userID  is the PK
    @Column(nullable = false, unique = true) // mark that userID column can not null and unique
    private String accountID;

    @Column( nullable = false)
    private String password;
    private String name;
    private int roleID;
    private String note;
    private String sex;
    private Date birthdate;
    private String address;
    private String phone;
    private double total_donation;



}
