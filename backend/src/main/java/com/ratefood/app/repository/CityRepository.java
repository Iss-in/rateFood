package com.ratefood.app.repository;

import com.ratefood.app.entity.City;
import com.ratefood.app.entity.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, String> {
    City findByName(String name);
    List<City> findAllByOrderByIdAsc(); // add this to your repository
    Page<City> findByNameStartingWithIgnoreCase(String prefix, Pageable pageable);
    Page<City> findAll(Pageable pageable);
}
