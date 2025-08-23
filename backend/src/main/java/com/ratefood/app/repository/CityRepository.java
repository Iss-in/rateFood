package com.ratefood.app.repository;

import com.ratefood.app.entity.City;
import com.ratefood.app.entity.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, String> {
    Optional<City> findByName(String name);
    List<City> findAllByOrderByIdAsc(); // add this to your repository
    Page<City> findByNameStartingWithIgnoreCase(String prefix, Pageable pageable);
    Page<City> findAll(Pageable pageable);

    @Query("SELECT c FROM City c WHERE LOWER(c.name) LIKE LOWER(CONCAT(:prefix, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT('% ', :prefix, '%'))")
    Page<City> findByNameWordPrefix(@Param("prefix") String prefix, Pageable pageable);

}
