package com.ratefood.app.repository;

import com.ratefood.app.entity.City;
import com.ratefood.app.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CityRepository extends JpaRepository<City, String> {
    City findByName(String name);
}
