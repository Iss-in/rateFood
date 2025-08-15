package com.ratefood.app.controller;

import com.ratefood.app.dto.request.RestaurantRequestDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.repository.RestaurantRepository;
import com.ratefood.app.service.RestaurentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurentService restaurantService;

    @PostMapping("/restaurant")
    public ResponseEntity<Restaurant> addRestaurent(@RequestBody RestaurantRequestDTO restaurantDTO) {
        Restaurant newRestaurant = restaurantService.addRestaurant(restaurantDTO);
        return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
    }


    @GetMapping("/restaurant/{city}")
    public PageResponseDTO<List<Restaurant>> getRestaurents(
        @PathVariable String city,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Float minRating,
        @RequestParam(required = false) Float maxRating,
        @RequestParam(required = false) Double currentLatitude,
        @RequestParam(required = false) Double currentLongitude,
        @RequestParam(required = false) Double maxDistanceKm,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @PageableDefault Pageable pageable
        )

    {
        PageResponseDTO<List<Restaurant>> restaurants = restaurantService.getRestaurants(name, city, minRating, maxRating,
                currentLatitude, currentLongitude, maxDistanceKm, pageable);
        return restaurants;
//        return new ResponseEntity<>(restaurants, HttpStatus.CREATED);
    }
}
