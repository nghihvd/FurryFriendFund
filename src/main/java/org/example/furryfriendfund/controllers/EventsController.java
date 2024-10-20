package org.example.furryfriendfund.controllers;

import org.apache.tomcat.util.http.parser.Authorization;
import org.example.furryfriendfund.events.Events;
import org.example.furryfriendfund.events.EventsDTO;
import org.example.furryfriendfund.events.EventsRepository;
import org.example.furryfriendfund.events.EventsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/events")
public class EventsController {

    @Autowired
    private EventsService eventsService;
    @Autowired
    private EventsRepository eventsRepository;

    @PostMapping("/addEvents")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> addEvent(@ModelAttribute EventsDTO eventsDTO) throws IOException {
        Events newEvent = eventsService.addEvent(eventsDTO);
        if (newEvent == null) {
            return ResponseUtils.createErrorRespone("Can not create a new event", null, HttpStatus.CONFLICT);
        }
        return ResponseUtils.createSuccessRespone("Send request successfully, please waiting for admin response", newEvent);
    }

    @DeleteMapping("/{eventID}/deleteEvents")
    @PreAuthorize("hasAuthority('2') or hasAuthority('1')" )
    public ResponseEntity<BaseResponse> deleteEvent(@PathVariable String eventID) {
        if (eventID == null || eventID.isEmpty()) {
            return ResponseUtils.createErrorRespone("Event ID is required", null, HttpStatus.BAD_REQUEST);
        }
        if (eventsService.deleteEvent(eventID)) {
            return ResponseUtils.createSuccessRespone("Event with ID " + eventID + " has been deleted", null);
        }
        return ResponseUtils.createErrorRespone("Can not delete a event", null, HttpStatus.CONFLICT);
    }

    @PostMapping(path = "/{eventID}/updateEvents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('2')" )
    public ResponseEntity<BaseResponse> updateEvents(@PathVariable String eventID, @ModelAttribute EventsDTO eventsDTO) throws IOException {

        eventsRepository.findById(eventID);

        // Gọi tầng service để cập nhật sự kiện
        Events updatedEvent = eventsService.updateEvents(eventID, eventsDTO);



        // Trả về kết quả
        return updatedEvent != null
                ? ResponseUtils.createSuccessRespone("Update successfully 😀", updatedEvent)
                : ResponseUtils.createErrorRespone("Update failed", null, HttpStatus.NOT_FOUND);
    }


//    public ResponseEntity<BaseResponse> updateEvents(@PathVariable String eventID, @ModelAttribute EventsDTO eventsDTO) throws IOException {
//
//
//
//
//
//        // Cập nhật thông tin từ DTO, ngoại trừ image và eventID
//        BeanUtils.copyProperties(eventsDTO, existingEvent, "image", "eventID");
//
//        // Xử lý cập nhật hình ảnh nếu có
//        if (eventsDTO.getImage() != null && !eventsDTO.getImage().isEmpty()) {
//            String newImageUrl = saveImage(eventsDTO.getImage());
//            existingEvent.setImg_url(newImageUrl);
//        }
//
//        // Cập nhật sự kiện
//        existingEvent.setStatus("Updating");
//        Events updatedEvent = eventsService.updateEvents(existingEvent);
//
//        if (updatedEvent != null) {
//            // Gửi thông báo với thông tin cũ và mới
//            return ResponseUtils.createSuccessRespone("Update request sent successfully", updatedEvent);
//        }
//
//        return ResponseUtils.createErrorRespone("Failed to update event", null, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @GetMapping("/showEvents")


    public ResponseEntity<BaseResponse> showEvents() {
        if (eventsService.showEvents() != null) {
            return ResponseUtils.createSuccessRespone("Show events successfully", eventsService.showEvents());
        }

        return ResponseUtils.createErrorRespone("Can not show events", null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/showEventAdmin")
    @PreAuthorize("hasAuthority('1') or hasAuthority('2')" )

    public ResponseEntity<BaseResponse> showEventAdmin() {
        if (eventsService.showEventsAdmin() != null) {
            return ResponseUtils.createSuccessRespone("Show events admin successfully", eventsService.showEventsAdmin());
        }
        return ResponseUtils.createErrorRespone("Can not show events", null, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{eventID}/status")
    @PreAuthorize("hasAuthority('1')" )

    public ResponseEntity<BaseResponse> updateStatus(@PathVariable String eventID,@RequestParam boolean status)
    {

        if (eventsService.getEvent(eventID) != null) {
            if(status){
                Events eventAccept = eventsService.acceptEventUpdating(eventID);
                if(eventAccept != null) {
                return ResponseUtils.createSuccessRespone("Accept successfully", eventAccept);}
            }else{
                boolean eventReject = eventsService.rejectEvent(eventID);
                if(eventReject){
                    return ResponseUtils.createSuccessRespone("Reject successfully", null);
                }
            }
        }
        return ResponseUtils.createErrorRespone("Can not update status event", null, HttpStatus.NOT_FOUND);
    }


}
