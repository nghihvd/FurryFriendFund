package org.example.furryfriendfund.events;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;


@Data // automatic create getter,setter, toString, hashCode
@NoArgsConstructor // create default constructor_ User()
@AllArgsConstructor // create constructor for all properties_ User(....)
public class EventsDTO {


    private Date start_date;
    private Date end_date;
    private String event_name;
    private String description;
    private String status;
    private String location;
    private double total_donation;

    @JsonIgnore
    private MultipartFile image;
}
