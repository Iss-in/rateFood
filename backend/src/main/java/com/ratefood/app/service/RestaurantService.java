package com.ratefood.app.service;

import com.ratefood.app.converter.RestaurantConverter;
import com.ratefood.app.dto.request.RestaurantRequestDTO;
import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.dto.response.RestaurantResponseDTO;
import com.ratefood.app.entity.*;
import com.ratefood.app.enums.ImageType;
import com.ratefood.app.repository.CityRepository;
import com.ratefood.app.repository.FavouriteRestaurantRepository;
import com.ratefood.app.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private FavouriteRestaurantRepository favouriteRestaurantRepository;

    @Autowired
    private RestaurantConverter restaurantConverter;

    @Autowired
    private ImageService imageService;

    public PageResponseDTO<List<RestaurantResponseDTO>> getRestaurants(
            String name,
            String city,
            Float minRating,
            Float maxRating,
            Double currentLatitude,
            Double currentLongitude,
            Double maxDistanceKm,
            Pageable pageable,
            Long userId
    ){
        Page<Restaurant> restaurants = restaurantRepository.getRestaurants(name, city, minRating, maxRating, currentLatitude,
                currentLongitude, maxDistanceKm, pageable);

        PageResponseDTO<List<RestaurantResponseDTO>> dto = new PageResponseDTO<>();
        dto.setData(restaurants.getContent().stream().map(restaurant -> {
            try {
                return restaurantConverter.fromRestaurantToRestaurantResponseDTO(restaurant, userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(java.util.stream.Collectors.toList()));
        dto.setTotalPages(restaurants.getTotalPages());
        dto.setTotalElements((int) restaurants.getTotalElements());
        dto.setCurrentPage(restaurants.getNumber());
        return dto;
    }

    public Restaurant addRestaurant(RestaurantRequestDTO restaurantDTO) throws Exception {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        boolean isAdmin = authentication.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
//
//        restaurantDTO.setDraft(isAdmin);  // true if admin, false if not

        String cityName = restaurantDTO.getCity();
        City city = cityRepository.findByName(cityName);
        Restaurant restaurantEntity = Restaurant.builder()
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
        Restaurant restaurantCreated = restaurantRepository.save(restaurantEntity);
        if(restaurantDTO.getImage() != null){
            //TODO: can compress image to a size ?
            String key = ImageType.RESTAURANT + "/" + String.valueOf(restaurantCreated.getId()) ;
            imageService.uploadImage(restaurantDTO.getImage(), key);
            String imageLink = imageService.getPresignedUrl(key, 10);
            restaurantCreated.setImage(imageLink);
        }

        return restaurantRepository.save(restaurantCreated);
    }


    public PageResponseDTO<List<RestaurantResponseDTO>> getFavouriteRestaurants(
            String city,
            Pageable pageable,
            Long userId
    ) {
        Page<Restaurant> restaurants = restaurantRepository.getRestaurantsByCIty(city, pageable);

        PageResponseDTO<List<RestaurantResponseDTO>> dto = new PageResponseDTO<>();
        List<RestaurantResponseDTO> dtoList = restaurants.getContent().stream().map(restaurant -> {
            try {
                return restaurantConverter.fromRestaurantToRestaurantResponseDTO(restaurant, userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        dto.setData(dtoList.stream().filter(dish -> dish.getIsFavourite() != null && dish.getIsFavourite()).collect(Collectors.toList()));
        dto.setTotalPages(restaurants.getTotalPages());
        dto.setTotalElements((int) restaurants.getTotalElements());
        dto.setCurrentPage(restaurants.getNumber());
        return dto;
    }


    public Boolean markRestaurantFavourite(long restaurantId , Long userId){

        FavouriteRestaurant favouriteRestaurant = favouriteRestaurantRepository.
                getFavouriteRestaurantByUserIdAndRestaurantId(userId, restaurantId).orElseGet(() -> {
                    FavouriteRestaurant newFavouriteRestaurant = FavouriteRestaurant.builder()
                            .restaurant(restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Dish not found")))
                            .userId(userId)
                            .build();
                    return newFavouriteRestaurant;
                });
        favouriteRestaurantRepository.save(favouriteRestaurant);
        return Boolean.TRUE;
    }

    public Boolean unMarkRestaurantFavourite(long restaurantId , Long userId){
        FavouriteRestaurant favouriteRestaurant = favouriteRestaurantRepository.getFavouriteRestaurantByUserIdAndRestaurantId(userId, restaurantId).
                orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        favouriteRestaurantRepository.delete(favouriteRestaurant);
        return Boolean.TRUE;
    }

}
