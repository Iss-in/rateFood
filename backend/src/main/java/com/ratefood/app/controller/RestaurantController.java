package com.ratefood.app.controller;

import com.ratefood.app.dto.request.RestaurantRequestDTO;
import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.dto.response.RestaurantResponseDTO;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.repository.RestaurantRepository;
import com.ratefood.app.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantService restaurantService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/restaurant")
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody RestaurantRequestDTO restaurantDTO) throws Exception {
        Restaurant newRestaurant = restaurantService.addRestaurant(restaurantDTO);
        return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/restaurant/draft")
    public ResponseEntity<Restaurant> addDraftRestaurant(@RequestBody RestaurantRequestDTO restaurantDTO) throws Exception {
        restaurantDTO.setDraft(false);
        Restaurant newRestaurant = restaurantService.addRestaurant(restaurantDTO);
        return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/restaurant/favourite/{restaurantId}")
    public ResponseEntity<Boolean> markRestaurantFavourite(@PathVariable long restaurantId, @RequestHeader("X-User-Id") Long userId) {
        return new ResponseEntity<>(restaurantService.markRestaurantFavourite(restaurantId, userId), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/restaurant/unFavourite/{restaurantId}")
    public ResponseEntity<Boolean> markRestaurantUnFavourite(@PathVariable long restaurantId, @RequestHeader("X-User-Id") Long userId) {
        return new ResponseEntity<>(restaurantService.unMarkRestaurantFavourite(restaurantId, userId), HttpStatus.ACCEPTED);
    }


    @GetMapping("/restaurant/{city}")
    public PageResponseDTO<List<RestaurantResponseDTO>> getRestaurants(
        @PathVariable String city,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Float minRating,
        @RequestParam(required = false) Float maxRating,
        @RequestParam(required = false) Double currentLatitude,
        @RequestParam(required = false) Double currentLongitude,
        @RequestParam(required = false) Double maxDistanceKm,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @PageableDefault Pageable pageable,
        @RequestHeader(value = "X-User-Id", required = false, defaultValue = "0") String userId
    )

    {
        PageResponseDTO<List<RestaurantResponseDTO>> restaurants = restaurantService.getRestaurants(name, city, minRating, maxRating,
                currentLatitude, currentLongitude, maxDistanceKm, pageable,  Long.parseLong(userId));
        return restaurants;
//        return new ResponseEntity<>(restaurants, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/restaurant/favourites/{city}")
    public PageResponseDTO<List<RestaurantResponseDTO>> getRestaurants(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PageableDefault Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "0") String userId
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//        log.info("User with roles: {} is getting dishes.", authentication.getAuthorities());
        PageResponseDTO<List<RestaurantResponseDTO>> restaurants = restaurantService.getFavouriteRestaurants(city, pageable, Long.parseLong(userId));
        return restaurants;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/restaurant/{restaurantId}")
    public ResponseEntity deleteRestaurant(@PathVariable Long restaurantId) {
        Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
        restaurantRepository.delete(restaurant);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }
}
