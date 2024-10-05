package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.events.Events;
import org.example.furryfriendfund.events.EventsDTO;
import org.example.furryfriendfund.events.EventsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventsController {

    private static final Path CURRENT_FOLDER =
            Paths.get(System.getProperty("user.dir"));

    @Autowired
    private EventsService eventsService;

    @PostMapping("/addEvents")
    public ResponseEntity<BaseResponse> addEvent(@ModelAttribute EventsDTO eventsDTO) throws IOException {
        ResponseEntity<BaseResponse> status = ResponseUtils.createSuccessRespone("Send request successfully, please waiting for admin response", eventsDTO);
        Path staticPath = Paths.get("static");
        Path imagePath = Paths.get("imageEvent");
        if(!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))){
            Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
        }
        Path  file = CURRENT_FOLDER.resolve(staticPath).resolve(imagePath)
                .resolve(Objects.requireNonNull(eventsDTO.getImage().getOriginalFilename()));
        try(OutputStream outputStream = Files.newOutputStream(file)){
            outputStream.write(eventsDTO.getImage().getBytes());
        }

        Events event = new Events();
        event.setEventID(UUID.randomUUID().toString().substring(0, 8));
        BeanUtils.copyProperties(eventsDTO, event,"image","eventID");// not copy img_url
        event.setImg_url(imagePath.resolve(eventsDTO.getImage().getOriginalFilename()).toString());

        Events newEvent = eventsService.addEvent(event);
        if(newEvent == null){
            return status = ResponseUtils.createErrorRespone("Can not create a new event", null, HttpStatus.CONFLICT);
        }
        return status;

    }



    @DeleteMapping("/deleteEvents")
    public void deleteEvent(String eventID){
        eventsService.deleteEvent(eventID);
    }

    @PutMapping("/updateEvents")
    public Events updateEvents(Events event){
        return null;
    }

    @GetMapping("/showEvents")
    public List<Events> showEvents(){
        return eventsService.showEvents();
    }



}
