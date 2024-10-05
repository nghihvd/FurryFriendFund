package org.example.furryfriendfund.events;

import java.sql.Date;
import java.util.List;

public interface IEventsService {
     Events addEvent(Events event);
     void deleteEvent(String eventID);
     Events updateEvents(Events event);
     List<Events> showEvents();
}
