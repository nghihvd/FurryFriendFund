package org.example.furryfriendfund.events;

import java.sql.Date;
import java.util.List;

public interface IEventsService {
    public Events addEvent(Events event);
    public void deleteEvent(String eventID);
    public Events updateEvents(Events event);
    public List<Events> showEvents();
}
