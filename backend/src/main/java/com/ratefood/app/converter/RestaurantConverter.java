package com.ratefood.app.converter;

import com.ratefood.app.dto.response.RestaurantResponseDTO;

import com.ratefood.app.entity.DraftRestaurant;
import com.ratefood.app.entity.FavouriteRestaurant;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.enums.ImageType;
import com.ratefood.app.repository.FavouriteRestaurantRepository;
import com.ratefood.app.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantConverter {


    @Autowired
    FavouriteRestaurantRepository favouriteRestaurantRepository;

    @Autowired
    private ImageService imageService;

    public RestaurantResponseDTO fromRestaurantToRestaurantResponseDTO(Restaurant restaurant){
        RestaurantResponseDTO responseDto = RestaurantResponseDTO.builder()
                .name(restaurant.getName())
                .id(restaurant.getId())
                .description(restaurant.getDescription())
                .cuisine(restaurant.getCuisine())
                .tags(restaurant.getTags())
                .rating(restaurant.getRating())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .image(restaurant.getImage())
                .city(restaurant.getCity())
                .favoriteCount(restaurant.getFavoriteCount())
                .build();
        return responseDto;
    }

    public RestaurantResponseDTO fromDraftRestaurantToRestaurantResponseDTO(DraftRestaurant restaurant){
        RestaurantResponseDTO responseDto = RestaurantResponseDTO.builder()
                .name(restaurant.getName())
                .id(restaurant.getId())
                .description(restaurant.getDescription())
                .cuisine(restaurant.getCuisine())
                .tags(restaurant.getTags())
                .rating(restaurant.getRating())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .image(restaurant.getImage())
                .city(restaurant.getCity())
                .build();
        return responseDto;
    }

    public RestaurantResponseDTO fromRestaurantToRestaurantResponseDTO(Restaurant restaurant, Long userId) throws Exception {
        RestaurantResponseDTO responseDto = RestaurantResponseDTO.builder()
                .name(restaurant.getName())
                .id(restaurant.getId())
                .description(restaurant.getDescription())
                .cuisine(restaurant.getCuisine())
                .tags(restaurant.getTags())
                .rating(restaurant.getRating())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
//                .image(restaurant.getImage())
                .image(imageService.getPresignedUrl(ImageType.RESTAURANT + "/" + String.valueOf(restaurant.getId()) , 100))
                .city(restaurant.getCity())
                .favoriteCount(restaurant.getFavoriteCount())
                .build();
//        if(restaurant.getImage() != null && !restaurant.getImage().isBlank())
//            responseDto.setImage(restaurant.getImage());
//        else
//        responseDto.setImage(imageService.getPresignedUrl(ImageType.DRAFT_RESTAURANT + "/" + String.valueOf(restaurant.getId()) , 100));

        if(userId != 0) {
            List<FavouriteRestaurant> favouriteDishes = favouriteRestaurantRepository.getFavouriteRestaurantsByUserId(userId);
            if (favouriteDishes.stream().anyMatch(favRestaurant -> favRestaurant.getRestaurant().getId() == restaurant.getId())) {
                responseDto.setIsFavourite(true);
            }
        }
        return responseDto;
    }

}
