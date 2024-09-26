package org.example.furryfriendfund.pets;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetsRepository extends JpaRepository<Pets, String> {
    List<Pets> findByNameIgnoreCase(String name);
    List<Pets> findByCategoryID(int categoryID);
    List<Pets> findByBreed(String breed);
}

