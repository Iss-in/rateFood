package com.ratefood.app.converter;

import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.FavouriteDish;
import com.ratefood.app.entity.FavouriteRestaurant;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.repository.FavouriteDishRepository;
import com.ratefood.app.repository.FavouriteRestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishConverter {

    @Autowired
    FavouriteDishRepository favouriteDishRepository;

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
    public DishResponseDTO fromDishtoDishResponseDTO(Dish dish, Long userId){
        DishResponseDTO responseDto = DishResponseDTO.builder()
                .name(dish.getName())
                .id(dish.getId())
                .description(dish.getDescription())
                .restaurant(dish.getRestaurant().getName())
                .tags(dish.getTags())
                .image(dish.getImage())
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
