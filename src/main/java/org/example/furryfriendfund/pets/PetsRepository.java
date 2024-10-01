package org.example.furryfriendfund.pets;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetsRepository extends JpaRepository<Pets, String> {
    List<Pets> findByNameContainingIgnoreCase(String name);
    List<Pets> findByBreedContainingIgnoreCase(String breed);
}

