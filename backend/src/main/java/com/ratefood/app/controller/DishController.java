package com.ratefood.app.controller;

import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.repository.DishRepository;
import com.ratefood.app.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DishController {
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public DishController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @PostMapping("/dish")
    public ResponseEntity<Dish> addDish(@RequestBody Dish dish) {
        dish.setId(1);
        Long restaurantId = dish.getRestaurant().getId();
        Restaurant restaurant =  restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        dish.setRestaurant(restaurant);
        Dish newDish = dishRepository.save(dish);
        return new ResponseEntity<>(newDish, HttpStatus.CREATED);
    }

}
