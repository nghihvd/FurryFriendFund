package org.example.furryfriendfund.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, String> {

    @Query("SELECT e FROM Events e WHERE LOWER(e.status) LIKE LOWER(:status)")
    List<Events> findByEventStatusIgnoreCase(@Param("status") String status);

    @Query("select a from Events a ")
    List<Events> showAllEvents();


}
