package org.example.furryfriendfund.events;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Data // automatic create getter,setter, toString, hashCode
@NoArgsConstructor // create default constructor_ User()
@AllArgsConstructor // create constructor for all properties_ User(....)
public class Events {
    @Id
    private String eventID;

    private Date start_date;
    private Date end_date;
    private String event_name;
    private String description;
    private String img_url;
    private String status;
    private String location;

}
