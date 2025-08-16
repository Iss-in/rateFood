package com.ratefood.app.service;

import com.ratefood.app.converter.DishConverter;
import com.ratefood.app.dto.request.DishRequestDTO;
import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.repository.DishRepository;
import com.ratefood.app.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.annotations.AttributeAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishService {

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    DishRepository dishRepository;

    @Autowired
    DishConverter dishConverter;

    public DishResponseDTO createDish(DishRequestDTO dto){

        String restaurantName = dto.getRestaurant();
        Restaurant restaurant =  restaurantRepository.findByName(restaurantName).orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        Dish dishEntity = Dish.builder()
                .name(dto.getName())
                .restaurant(restaurant)
                .tags(dto.getTags())
                .description(dto.getDescription())
                .build();
        if(dto.getImage() != null)
            dishEntity.setImage(dto.getImage());

        Dish dishCreated  = dishRepository.save(dishEntity);
        DishResponseDTO responseDto = dishConverter.fromDishtoDishResponseDTO(dishCreated);
        return responseDto;
    }

    public PageResponseDTO<List<DishResponseDTO>> getDishes(
            String name,
            String city,
            Float minRating,
            Float maxRating,
            Double currentLatitude,
            Double currentLongitude,
            Double maxDistanceKm,
            Pageable pageable
    ) {
        Page<Dish> dishes = dishRepository.getDishes(name, city, minRating, maxRating, currentLatitude,
                currentLongitude, maxDistanceKm, pageable);

        PageResponseDTO<List<DishResponseDTO>> dto = new PageResponseDTO<>();
        dto.setData(dishes.getContent().stream().map(dish -> dishConverter.fromDishtoDishResponseDTO(dish)).collect(Collectors.toList()));
        dto.setTotalPages(dishes.getTotalPages());
        dto.setTotalElements((int) dishes.getTotalElements());
        dto.setCurrentPage(dishes.getNumber());
        return dto;
    }
}
