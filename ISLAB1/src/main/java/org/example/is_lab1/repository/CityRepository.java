package org.example.is_lab1.repository;

import org.example.is_lab1.models.entity.MagicCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<MagicCity, Long> {
    Optional<MagicCity> findFirstByName(String name);
}
