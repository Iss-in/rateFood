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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class DishController {
//    private static final Logger log = LoggerFactory.getLogger(DishController.class);
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishService dishService;

    public DishController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/dish")
    public ResponseEntity<DishResponseDTO> addDish(@RequestBody DishRequestDTO dishDTO) {
        DishResponseDTO newDish = dishService.createDish(dishDTO);
        return new ResponseEntity<>(newDish, HttpStatus.CREATED);
    }

//    @PreAuthorize("hasRole('ADMIN')")
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("User with roles: {} is getting dishes.", authentication.getAuthorities());
        PageResponseDTO<List<DishResponseDTO>> dishes = dishService.getDishes(name, city, minRating, maxRating,
                currentLatitude, currentLongitude, maxDistanceKm, pageable);
        return dishes;
//        return new ResponseEntity<>(restaurants, HttpStatus.CREATED);
    }

}
