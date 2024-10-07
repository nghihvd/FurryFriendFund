package org.example.furryfriendfund.events;

import java.sql.Date;
import java.util.List;

public interface IEventsService {
     Events addEvent(Events event);
     boolean deleteEvent(String eventID);
     Events updateEvents(Events event);
     List<Events> showEvents();
     List<Events> showEventsAdmin();
     Events getEvent(String eventID);
     Events acceptEventUpdating(String eventID);
     boolean rejectEvent(String eventID);
}
