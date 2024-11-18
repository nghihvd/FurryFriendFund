package org.example.furryfriendfund.events;

import jdk.jfr.Event;

import java.io.IOException;
import java.util.List;

public interface IEventsService {
     Events addEvent(EventsDTO eventsDTO) throws IOException;
     Events save(Events event);
     boolean deleteEvent(String eventID);
     Events updateEvents(String eventID, EventsDTO eventsDTO) throws IOException;
     List<Events> showEvents();
     List<Events> showEventsAdmin();
     Events getEvent(String eventID);
     Events acceptEventUpdating(String eventID);
     boolean rejectEvent(String eventID);
     List<Events> showEventsByWaitingUpdating();
     //void deleteEventAndNotifications(String eventId);
     int countEvents(String status);
}
