package com.ratefood.app.migrations;

import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.enums.ImageType;
import com.ratefood.app.repository.CityRepository;
import com.ratefood.app.repository.DishRepository;
import com.ratefood.app.repository.RestaurantRepository;
import com.ratefood.app.service.CityService;
import com.ratefood.app.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.util.List;

@Slf4j
@RestController()
@RequestMapping("/api")
public class imageMigrations {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ImageService imageService;


    @PostMapping("/migrate-images")
    public void migrateImagestoMinio(
    ) throws Exception {
        List <Dish> dishes = dishRepository.findAll();
        for(Dish dish:dishes){
            if(!dish.getImage().contains("foodapp/DISH")){
                try {
                    String key = ImageType.DISH + "/" + String.valueOf(dish.getId());
                    imageService.uploadImage(dish.getImage(), key);
                    String imageLink = imageService.getPresignedUrl(key, 10);
                    dish.setImage(imageLink);
                    dishRepository.save(dish);
                }
                catch (Exception e){
                    log.error("image migration failed for dish {}", dish.getName());
                }
            }
        }

        List <Restaurant> restaurants = restaurantRepository.findAll();
        for(Restaurant restaurant:restaurants){
            if(!restaurant.getImage().contains("foodapp/RESTAURANT")){
                try {
                    String key = ImageType.RESTAURANT + "/" + String.valueOf(restaurant.getId());
                    imageService.uploadImage(restaurant.getImage(), key);
                    String imageLink = imageService.getPresignedUrl(key, 10);
                    restaurant.setImage(imageLink);
                    restaurantRepository.save(restaurant);
                }
                catch (Exception e){
                        log.error("image migration failed for restaurant {}", restaurant.getName());
                }
            }
        }

    }

}
