package org.example.furryfriendfund.appointments;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Data // automatic create getter,setter, toString, hashCode
@NoArgsConstructor // create default constructor_ User()
@AllArgsConstructor // create constructor for all properties_ User(....)
@Table(name="appointment",uniqueConstraints = @UniqueConstraint(columnNames = "appointID")) // ánh xạ thực thể này đến bảng users để tránh trường hợp tự động tạo bảng mới khi
//không tự động nhận diện được bảng
public class Appointments {
    @Id
    @Column(nullable = false, unique = true)
    private String appointID;

    private LocalDateTime date_time;
    private String accountID;
    private boolean status;
    private String petID;
    private String staffID;


}
