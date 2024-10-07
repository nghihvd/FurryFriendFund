package org.example.furryfriendfund.events;


import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EventsService implements IEventsService {


    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EventsRepository eventRepo;


    @Override
    public Events addEvent(Events event) {
        Events a_event = null;
        if (!eventRepo.existsById(event.getEventID())) {
            event.setStatus("Waiting");
            a_event = eventRepo.save(event);
            notificationService.waitingEventsNoti(a_event);
        }
        return a_event;
    }

    @Override
    public Events acceptEventUpdating(String eventID) {
        Events eventOpt = getEvent(eventID);
        if (eventOpt.getStatus().equalsIgnoreCase("Waiting")) {
            eventOpt.setStatus("Updating");
        }
        return eventRepo.save(eventOpt);
    }

    @Override
    public boolean deleteEvent(String eventID) {
        boolean success = false;
        if (eventRepo.existsById(eventID)) {
            eventRepo.deleteById(eventID);
            success = true;
        }
        return success;
    }

    /**
     * @param event là data mới của event cũ
     * @return
     */
    @Override
    public Events updateEvents(Events event) {
        // Tìm sự kiện trong cơ sở dữ liệu dựa trên eventID
        Events existingEventOpt = getEvent(event.getEventID());

        // Nếu sự kiện tồn tại, tiến hành cập nhật
        if (existingEventOpt != null && event.getStatus().equalsIgnoreCase("Updating")) {
            Events existingEvent = existingEventOpt;

            // So sánh và cập nhật các trường khác nhau
            if (!existingEvent.getEvent_name().equalsIgnoreCase(event.getEvent_name()) && event.getEvent_name() != null) {
                existingEvent.setEvent_name(event.getEvent_name());
            }
            if (!existingEvent.getStart_date().equals(event.getStart_date()) && event.getStart_date() != null) {
                existingEvent.setStart_date(event.getStart_date());
            }
            if (!existingEvent.getEnd_date().equals(event.getEnd_date()) && event.getEnd_date() != null) {
                existingEvent.setEnd_date(event.getEnd_date());
            }
            if (!existingEvent.getDescription().equalsIgnoreCase(event.getDescription()) && event.getDescription() != null) {
                existingEvent.setDescription(event.getDescription());
            }
            if (!existingEvent.getImg_url().equalsIgnoreCase(event.getImg_url()) && event.getImg_url() != null) {
                existingEvent.setImg_url(event.getImg_url());
            }


            //gửi thông báo cho admin chấp nhận
            notificationService.updateEventsNoti(existingEvent);

            // Lưu lại sự kiện đã cập nhật vào cơ sở dữ liệu
            return eventRepo.save(existingEvent);
        }

        // Trả về null nếu sự kiện không tồn tại
        return null;
    }


    @Override
    public List<Events> showEvents() {
        return eventRepo.findByEventStatusIgnoreCase("Available");
    }

    @Override
    public List<Events> showEventsAdmin() {
        return eventRepo.showAllEvents();
    }

    @Override
    public Events getEvent(String eventID) {
        return eventRepo.findById(eventID).orElse(null);
    }


    @Override
    public boolean rejectEvent(String eventID) {
        eventRepo.deleteById(eventID);
        Events events = getEvent(eventID);
        if (events == null) {
            return true;
        }
        return false;
    }


}
