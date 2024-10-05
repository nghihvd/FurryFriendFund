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
        if (!eventRepo.existsById(event.getEventID()) && event.getStatus().equalsIgnoreCase("Available")) {
            event.setStatus("Unavailable");
            a_event = eventRepo.save(event);
            notificationService.createEventsNoti(a_event);
        }
        return a_event;
    }

    @Override
    public void deleteEvent(String eventID) {
        eventRepo.deleteById(eventID);
    }

    @Override
    public Events updateEvents(Events event) {
        Events a_event = new Events();
        if (eventRepo.existsById(event.getEventID())) {
            a_event.setEventID(event.getEventID());
            if(!a_event.getEvent_name().equalsIgnoreCase(event.getEvent_name())) {
                a_event.setEvent_name(event.getEvent_name());
            }
            if(!a_event.getStart_date().equals(event.getStart_date()))
            {
                a_event.setStart_date(event.getStart_date());
            }
            if(!a_event.getEnd_date().equals(event.getEnd_date())){
                a_event.setEnd_date(event.getEnd_date());
            }
            if(!a_event.getDescription().equalsIgnoreCase(event.getDescription())) {
                a_event.setDescription(event.getDescription());
            }
            if(!a_event.getImg_url().equalsIgnoreCase(event.getImg_url())) {
                a_event.setImg_url(event.getImg_url());
            }

        }
        eventRepo.deleteById(event.getEventID());

        return eventRepo.save(a_event);
    }

    @Override
    public List<Events> showEvents() {
        List<Events> events = eventRepo.findAll();
        return events;
    }


}
