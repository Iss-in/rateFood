package com.ratefood.app.converter;

import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.entity.*;
import com.ratefood.app.enums.ImageType;
import com.ratefood.app.repository.FavouriteDishRepository;
import com.ratefood.app.repository.FavouriteRestaurantRepository;
import com.ratefood.app.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishConverter {

    @Autowired
    FavouriteDishRepository favouriteDishRepository;
    @Autowired
    private ImageService imageService;

    public DishResponseDTO fromDishtoDishResponseDTO(Dish dish){
        DishResponseDTO responseDto = DishResponseDTO.builder()
                .name(dish.getName())
                .id(dish.getId())
                .description(dish.getDescription())
                .restaurant(dish.getRestaurant().getName())
                .tags(dish.getTags())
                .image(dish.getImage())
                .build();
        return responseDto;
    }

    public DishResponseDTO fromDraftDishtoDishResponseDTO(DraftDish dish) throws Exception {
        DishResponseDTO responseDto = DishResponseDTO.builder()
                .name(dish.getName())
                .id(dish.getId())
                .description(dish.getDescription())
                .restaurant(dish.getRestaurant().getName())
                .tags(dish.getTags())
//                .image(dish.getImage())
                .image(imageService.getPresignedUrl(ImageType.DRAFT_DISH + "/" + String.valueOf(dish.getId()) , 100))
                .build();
        return responseDto;
    }

    public DishResponseDTO fromDishtoDishResponseDTO(Dish dish, Long userId) throws Exception {
        DishResponseDTO responseDto = DishResponseDTO.builder()
                .name(dish.getName())
                .id(dish.getId())
                .description(dish.getDescription())
                .restaurant(dish.getRestaurant().getName())
                .tags(dish.getTags())
//                .image(dish.getImage())
                .image(imageService.getPresignedUrl(ImageType.DISH + "/" + String.valueOf(dish.getId()) , 100))
                .favoriteCount(dish.getFavoriteCount())
                .build();
        if(userId != 0) {
            List<FavouriteDish> favouriteDishes = favouriteDishRepository.getFavouriteDishesByUserId(userId);
            if (favouriteDishes.stream().anyMatch(favDish -> favDish.getDish().getId() == dish.getId())) {
                responseDto.setIsFavourite(true);
            }
        }
        return responseDto;
    }

}
