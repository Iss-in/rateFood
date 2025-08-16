package com.ratefood.app.converter;

import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.entity.Dish;
import org.springframework.stereotype.Service;

@Service
public class DishConverter {

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
}
