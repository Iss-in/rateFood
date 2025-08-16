package com.ratefood.app.controller;

import com.ratefood.app.dto.request.DishRequestDTO;
import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.repository.DishRepository;
import com.ratefood.app.repository.RestaurantRepository;
import com.ratefood.app.service.DishService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DishController {
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishService dishService;

    public DishController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @PostMapping("/dish")
    public ResponseEntity<DishResponseDTO> addDish(@RequestBody DishRequestDTO dishDTO) {
        DishResponseDTO newDish = dishService.createDish(dishDTO);
        return new ResponseEntity<>(newDish, HttpStatus.CREATED);
    }

    @GetMapping("/dish/{city}")
    public PageResponseDTO<List<DishResponseDTO>> getdishes(
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
        PageResponseDTO<List<DishResponseDTO>> dishes = dishService.getDishes(name, city, minRating, maxRating,
                currentLatitude, currentLongitude, maxDistanceKm, pageable);
        return dishes;
//        return new ResponseEntity<>(restaurants, HttpStatus.CREATED);
    }

}
