package org.example.furryfriendfund.accounts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Entity
@Data // automatic create getter,setter, toString, hashCode
@NoArgsConstructor // create default constructor_ User()
@AllArgsConstructor // create constructor for all properties_ User(....)
@Table (name="accounts") // ánh xạ thực thể này đến bảng users để tránh trường hợp tự động tạo bảng mới khi
//không tự động nhận diện được bảng
public class Accounts {
    @Id // mark userID  is the PK
    @Column(nullable = false, unique = true) // mark that userID column can not null and unique
    private String accountID;

    @Column( nullable = false)
    private String password;
    @Column(name ="name")
    private String fullName;
    @Column(name="roleid")
    private int roleId;
    private String note;
    private String sex;
    @Column(name="birthdate")
    private Date birthday;
    private String address;
    @Column(name="phone")
    private String phoneNumber;
    @Column(name="total")
    private double totalDonate;

}
