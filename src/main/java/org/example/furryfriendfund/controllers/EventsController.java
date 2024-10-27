package org.example.furryfriendfund.controllers;

import org.example.furryfriendfund.events.Events;
import org.example.furryfriendfund.events.EventsDTO;
import org.example.furryfriendfund.events.EventsService;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/events")
public class EventsController {

    @Autowired
    private EventsService eventsService;


    @GetMapping("/{eventId}/getEventById")
    public ResponseEntity<BaseResponse> getEventById(@RequestParam("eventId") String eventId) {
        Events foundEvent = eventsService.getEvent(eventId);
        if (foundEvent == null) {
            return ResponseUtils.createErrorRespone("Can not found", null, HttpStatus.NOT_FOUND);
        } else {
            return ResponseUtils.createSuccessRespone("Found event", foundEvent);
        }
    }


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
    @PreAuthorize("hasAuthority('2') or hasAuthority('1')")
    public ResponseEntity<BaseResponse> deleteEvent(@PathVariable String eventID) {
        if(eventsService.getEvent(eventID) == null) {
            return ResponseUtils.createErrorRespone("Can not found " + eventID, null, HttpStatus.NOT_FOUND);
        }
        if (eventID == null || eventID.isEmpty()) {
            return ResponseUtils.createErrorRespone("Event ID is required", null, HttpStatus.BAD_REQUEST);
        }else {
            if(eventsService.getEvent(eventID).getStatus().equalsIgnoreCase("Ending")) {
                return ResponseUtils.createErrorRespone("Event is already ended", null, HttpStatus.CONFLICT);
            }
            if (!eventsService.deleteEvent(eventID)) {
                return ResponseUtils.createSuccessRespone("Event with ID " + eventID + " has been change status to Ending", eventsService.getEvent(eventID));
            } else
                return ResponseUtils.createSuccessRespone("Event with ID " + eventID + " has been REMOVE", null);
        }
    }

//    @DeleteMapping("/{eventId}/deleteEventNoti")
//    @PreAuthorize("hasAuthority('1')")  // Giả sử quyền admin
//    public ResponseEntity<BaseResponse> deleteEventAndNoti(@PathVariable String eventId) {
//        try {
//            // Method này sẽ xóa cả event và notifications liên quan
//            eventsService.deleteEventAndNotifications(eventId);
//
//            return ResponseUtils.createSuccessRespone(
//                    "Event and related notifications deleted successfully",
//                    null
//            );
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return ResponseUtils.createErrorRespone(
//                    "Failed to delete event and notifications",
//                    null,
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }

    @PostMapping(path = "/{eventID}/updateEvents")
    @PreAuthorize("hasAuthority('2')")
    public ResponseEntity<BaseResponse> updateEvents(@PathVariable String eventID, @ModelAttribute EventsDTO eventsDTO) throws IOException {
        // Gọi tầng service để cập nhật sự kiện
        Events updatedEvent = eventsService.updateEvents(eventID, eventsDTO);


        // Trả về kết quả
        return updatedEvent != null
                ? ResponseUtils.createSuccessRespone("Update successfully 😀", updatedEvent)
                : ResponseUtils.createErrorRespone("Update failed", null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/showEvents")
    public ResponseEntity<BaseResponse> showEvents() {
        if (eventsService.showEvents() != null) {
            return ResponseUtils.createSuccessRespone("Show events successfully", eventsService.showEvents());
        }

        return ResponseUtils.createErrorRespone("Can not show events", null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/showEventAdmin")
    @PreAuthorize("hasAuthority('1') or hasAuthority('2')")

    public ResponseEntity<BaseResponse> showEventAdmin() {
        if (eventsService.showEventsAdmin() != null) {
            return ResponseUtils.createSuccessRespone("Show events admin successfully", eventsService.showEventsAdmin());
        }
        return ResponseUtils.createErrorRespone("Can not show events", null, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{eventID}/status")
    @PreAuthorize("hasAuthority('1')")

    public ResponseEntity<BaseResponse> updateStatus(@PathVariable String eventID, @RequestParam boolean status) {

        if (eventsService.getEvent(eventID) != null) {
            if (status) {
                Events eventAccept = eventsService.acceptEventUpdating(eventID);
                if (eventAccept != null) {
                    return ResponseUtils.createSuccessRespone("Accept successfully", eventAccept);
                }
            } else {
                boolean eventReject = eventsService.rejectEvent(eventID);
                if (eventReject) {

                    return ResponseUtils.createSuccessRespone("Reject successfully", null);
                }
            }
        }
        return ResponseUtils.createErrorRespone("Can not update status event", null, HttpStatus.NOT_FOUND);
    }


}
