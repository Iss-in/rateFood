package com.ratefood.app.service;

import com.ratefood.app.dto.request.RestaurantRequestDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.City;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.repository.CityRepository;
import com.ratefood.app.repository.RestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CityRepository cityRepository;

    public PageResponseDTO<List<Restaurant>> getRestaurants(
            String name,
            String city,
            Float minRating,
            Float maxRating,
            Double currentLatitude,
            Double currentLongitude,
            Double maxDistanceKm,
            Pageable pageable
    ){
        Page<Restaurant> restaurants = restaurantRepository.getRestaurants(name, city, minRating, maxRating, currentLatitude,
                currentLongitude, maxDistanceKm, pageable);

        PageResponseDTO<List<Restaurant>> dto = new PageResponseDTO<>();
        dto.setData(restaurants.getContent());
        dto.setTotalPages(restaurants.getTotalPages());
        dto.setTotalElements((int) restaurants.getTotalElements());
        dto.setCurrentPage(restaurants.getNumber());
        return dto;
    }

    public Restaurant addRestaurant(RestaurantRequestDTO restaurantDTO){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        restaurantDTO.setDraft(isAdmin);  // true if admin, false if not

        String cityName = restaurantDTO.getCity();
        City city = cityRepository.findByName(cityName);
        Restaurant restaurant = Restaurant.builder()
//                .id(1)
                .name(restaurantDTO.getName())
                .cuisine(restaurantDTO.getCuisine())
                .description(restaurantDTO.getDescription())
                .tags(restaurantDTO.getTags())
                .image(restaurantDTO.getImage())
                .city(city)
//                .longitude(restaurantDTO.getLongitude()) TODO: get location from user
//                .latitude(restaurantDTO.getLatitude())
                .build();
        return restaurantRepository.save(restaurant);
    }
}
