package com.ratefood.app.service;

import com.ratefood.app.converter.DishConverter;
import com.ratefood.app.dto.request.DishRequestDTO;
import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.FavouriteDish;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.repository.DishRepository;
import com.ratefood.app.repository.FavouriteDishRepository;
import com.ratefood.app.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishService {

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    DishRepository dishRepository;

    @Autowired
    DishConverter dishConverter;

    @Autowired
    private FavouriteDishRepository favouriteDishRepository;

    public DishResponseDTO createDish(DishRequestDTO dto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        dto.setDraft(isAdmin);  // true if admin, false if not

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

    public DishResponseDTO updateDish(DishRequestDTO dto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        String restaurantName = dto.getRestaurant();
        Restaurant restaurant =  restaurantRepository.findByName(restaurantName).orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        Dish dishEntity = Dish.builder()
                .id(dto.getId())
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
            Boolean favourites,
            Pageable pageable,
            Long userId
    ) {
        Page<Dish> dishes = dishRepository.getDishes(name, city, minRating, maxRating, currentLatitude,
                currentLongitude, maxDistanceKm, pageable);

        PageResponseDTO<List<DishResponseDTO>> dto = new PageResponseDTO<>();
        dto.setData(dishes.getContent().stream().map(dish -> dishConverter.fromDishtoDishResponseDTO(dish, userId)).collect(Collectors.toList()));
        dto.setTotalPages(dishes.getTotalPages());
        dto.setTotalElements((int) dishes.getTotalElements());
        dto.setCurrentPage(dishes.getNumber());
        return dto;
    }

    public PageResponseDTO<List<DishResponseDTO>> getFavouriteDishes(
            String city,
            Pageable pageable,
            Long userId
    ) {
//        Page<Dish> dishes = dishRepository.getDishesByCIty(city, pageable);
        Page<Dish> dishes = dishRepository.findAll( pageable);

        PageResponseDTO<List<DishResponseDTO>> dto = new PageResponseDTO<>();
        List<DishResponseDTO> dtoList = dishes.getContent().stream().map(dish -> dishConverter.fromDishtoDishResponseDTO(dish, userId)).collect(Collectors.toList());
        dto.setData(dtoList.stream().filter(dish -> dish.getIsFavourite() != null && dish.getIsFavourite()).collect(Collectors.toList()));
        dto.setTotalPages(dishes.getTotalPages());
        dto.setTotalElements((int) dishes.getTotalElements());
        dto.setCurrentPage(dishes.getNumber());
        return dto;
    }

    public Boolean markDishFavourite(long dishId , Long userId){
        FavouriteDish favouriteDish = favouriteDishRepository.getFavouriteDishesByUserIdAndDishId(userId, dishId).orElseGet(() -> {
            FavouriteDish newFavourite = FavouriteDish.builder()
                    .dish(dishRepository.findById(dishId).orElseThrow(() -> new EntityNotFoundException("Dish not found")))
                    .userId(userId)
                    .build();
            return newFavourite;
        });
        favouriteDishRepository.save(favouriteDish);
        return Boolean.TRUE;
    }

    public Boolean unMarkDishFavourite(long dishId , Long userId){
        FavouriteDish favouriteDish = favouriteDishRepository.getFavouriteDishesByUserIdAndDishId(userId, dishId).
                orElseThrow(() -> new EntityNotFoundException("Dish not found"));
        favouriteDishRepository.delete(favouriteDish);
        return Boolean.TRUE;
    }
}
