package org.example.furryfriendfund.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, String> {

    @Query("SELECT e FROM Events e WHERE LOWER(e.status) IN :statuses")
    List<Events> findByEventStatusInIgnoreCase(@Param("statuses") List<String> statuses);

    @Query("select a from Events a ")
    List<Events> showAllEvents();

    @Query("SELECT COUNT(e) FROM Events e WHERE e.status ='Waiting'")
    int countWaitingEvents();

    @Query("SELECT COUNT(e) FROM Events e WHERE e.status ='Updating'")
    int countUpdatingEvents();

    @Query("SELECT COUNT(e) FROM Events e WHERE e.status ='Published'")
    int countPublishedEvents();

    @Query("SELECT COUNT(e) FROM Events e WHERE e.status ='Ending'")
    int countEndingEvents();
}
