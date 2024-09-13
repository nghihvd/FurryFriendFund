package org.furryFriendFund.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Entity
@Data // automatic create getter,setter, toString, hashCode
@NoArgsConstructor // create default constructor_ User()
@AllArgsConstructor // create constructor for all properties_ User(....)
public class UsersDTO {
    @Id // mark userID  is the PK
    @Column(nullable = false, unique = true) // mark that userID column can not null and unique
    private String userID;

    private String password;
    private String fullName;
    private int roleID;
    private String note;
    private String sex;
    private Date birthday;
    private String address;
    private String phoneNumber;


}
