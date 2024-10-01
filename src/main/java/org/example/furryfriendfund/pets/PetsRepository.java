package org.example.furryfriendfund.pets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetsRepository extends JpaRepository<Pets, String> {
    @Query("select a from Pets a where REPLACE(LOWER(a.name), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:name, ' ', ''), '%'))")
    List<Pets> findByNameIgnoreCaseAndTrimmed(@Param("name") String name);

}

