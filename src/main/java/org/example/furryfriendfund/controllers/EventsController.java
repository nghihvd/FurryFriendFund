package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.events.Events;
import org.example.furryfriendfund.events.EventsDTO;
import org.example.furryfriendfund.events.EventsRepository;
import org.example.furryfriendfund.events.EventsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
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
    @Autowired
    private EventsRepository eventsRepository;

    @PostMapping("/addEvents")
    public ResponseEntity<BaseResponse> addEvent(@ModelAttribute EventsDTO eventsDTO) throws IOException {

        Path staticPath = Paths.get("static");
        Path imagePath = Paths.get("imageEvent");
        if (!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
        }
        Path file = CURRENT_FOLDER.resolve(staticPath).resolve(imagePath)
                .resolve(Objects.requireNonNull(eventsDTO.getImage().getOriginalFilename()));
        try (OutputStream outputStream = Files.newOutputStream(file)) {
            outputStream.write(eventsDTO.getImage().getBytes());
        }

        Events event = new Events();
        event.setEventID(UUID.randomUUID().toString().substring(0, 8));
        BeanUtils.copyProperties(eventsDTO, event, "image", "eventID");// not copy img_url
        event.setImg_url(imagePath.resolve(eventsDTO.getImage().getOriginalFilename()).toString());

        Events newEvent = eventsService.addEvent(event);
        if (newEvent == null) {
            return ResponseUtils.createErrorRespone("Can not create a new event", null, HttpStatus.CONFLICT);
        }
        return ResponseUtils.createSuccessRespone("Send request successfully, please waiting for admin response", eventsDTO);

    }

    @DeleteMapping("/{eventID}/deleteEvents")
    public ResponseEntity<BaseResponse> deleteEvent(@PathVariable String eventID) {
        if (eventID == null || eventID.isEmpty()) {
            return ResponseUtils.createErrorRespone("Event ID is required", null, HttpStatus.BAD_REQUEST);
        }
        if (eventsService.deleteEvent(eventID)) {
            return ResponseUtils.createSuccessRespone("Event with ID " + eventID + " has been deleted", null);
        }
        return ResponseUtils.createErrorRespone("Can not delete a event", null, HttpStatus.CONFLICT);
    }

    @PutMapping("/{eventID}/updateEvents")
    public ResponseEntity<BaseResponse> updateEvents(@PathVariable String eventID, @ModelAttribute EventsDTO eventsDTO) throws IOException {
        // Kiểm tra xem sự kiện có tồn tại không
        Events eventUpdate = eventsService.getEvent(eventID);
        if (eventUpdate == null) {
            return ResponseUtils.createErrorRespone("Event with ID " + eventID + " not found", null, HttpStatus.NOT_FOUND);
        } else {
            if (!eventUpdate.getStatus().equalsIgnoreCase("Updating")) {
                // Sao chép các thuộc tính từ eventsDTO sang events, trừ image và eventID
                BeanUtils.copyProperties(eventsDTO, eventUpdate, "image", "eventID");

                if (eventsDTO.getImage() != null && !eventsDTO.getImage().isEmpty()) {
                    // Tạo thư mục lưu hình ảnh nếu chưa tồn tại
                    Path staticPath = Paths.get("static");
                    Path imagePath = Paths.get("imageEvent");
                    if (!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))) {
                        Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
                    }

                    // Lưu ảnh vào thư mục
                    Path file = CURRENT_FOLDER.resolve(staticPath).resolve(imagePath)
                            .resolve(Objects.requireNonNull(eventsDTO.getImage().getOriginalFilename()));
                    try (OutputStream outputStream = Files.newOutputStream(file)) {
                        outputStream.write(eventsDTO.getImage().getBytes());
                    }

                    // Cập nhật URL của hình ảnh
                    eventUpdate.setImg_url(imagePath.resolve(eventsDTO.getImage().getOriginalFilename()).toString());
                }

                // Gọi phương thức cập nhật từ service
                Events updatedEvent = eventsService.updateEvents(eventUpdate);

                // Kiểm tra nếu việc cập nhật thành công
                if (updatedEvent != null) {
                    return ResponseUtils.createSuccessRespone("Update successfully 😀", updatedEvent);
                }
            }
        }
        return ResponseUtils.createErrorRespone("We don't have this event 😢", null, HttpStatus.CONFLICT);
    }

    @GetMapping("/showEvents")
    public ResponseEntity<BaseResponse> showEvents() {
        if (eventsService.showEvents() != null) {
            return ResponseUtils.createSuccessRespone("Show events successfully", eventsService.showEvents());
        }

        return ResponseUtils.createErrorRespone("Can not show events", null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/showEventAdmin")
    public ResponseEntity<BaseResponse> showEventAdmin() {
        if (eventsService.showEventsAdmin() != null) {
            return ResponseUtils.createSuccessRespone("Show events admin successfully", eventsService.showEventsAdmin());
        }
        return ResponseUtils.createErrorRespone("Can not show events", null, HttpStatus.NOT_FOUND);
    }

//    @PutMapping("/{eventID}/status")
//    public ResponseEntity<BaseResponse> updateStatus(@PathVariable String eventID,@RequestParam boolean status) throws IOException
//    {
//
//        if (eventsService.getEvent(eventID) != null) {
//            if(status){
//                Events eventAccept = eventsService.acceptEvent(eventID);
//                if(eventAccept != null) {
//                return ResponseUtils.createSuccessRespone("Accept successfully", eventAccept);}
//            }else{
//                boolean eventReject = eventsService.rejectEvent(eventID);
//                if(eventReject){
//                    return ResponseUtils.createSuccessRespone("Reject successfully", null);
//                }
//            }
//        }
//        return ResponseUtils.createErrorRespone("Can not update status event", null, HttpStatus.NOT_FOUND);
//    }


}
