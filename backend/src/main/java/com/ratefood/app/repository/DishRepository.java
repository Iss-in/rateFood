package com.ratefood.app.repository;

import com.ratefood.app.entity.Dish;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DishRepository extends JpaRepository<Dish, Long> {
}

