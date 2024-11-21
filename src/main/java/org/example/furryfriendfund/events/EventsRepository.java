package org.example.furryfriendfund.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, String> {

    @Query(value = "SELECT * FROM Events e WHERE LOWER(e.status) IN :statuses " +
            "ORDER BY CASE " +
            "WHEN e.status = 'Waiting' THEN 1 " +
            "WHEN e.status = 'Updating' THEN 2 " +
            "WHEN e.status = 'Published' THEN 3 " +
            "WHEN e.status = 'Ending' THEN 4 " +
            "ELSE 5 END",nativeQuery = true)
    List<Events> findByEventStatusInIgnoreCase(@Param("statuses") List<String> statuses);

    @Query("select e from Events e "+
            "ORDER BY CASE " +
            "WHEN e.status = 'Waiting' THEN 1 " +
            "WHEN e.status = 'Updating' THEN 2 " +
            "WHEN e.status = 'Published' THEN 3 " +
            "WHEN e.status = 'Ending' THEN 4 " +
            "ELSE 5 END")
    List<Events> showAllEvents();

    @Query(value = "SELECT COUNT(e) FROM Events e " +
            "WHERE e.status = :status")
    int countEvents(@Param("status") String status);
}
