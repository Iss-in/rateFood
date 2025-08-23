package com.ratefood.app.migrations;

import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.City;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.enums.ImageType;
import com.ratefood.app.repository.CityRepository;
import com.ratefood.app.repository.DishRepository;
import com.ratefood.app.repository.RestaurantRepository;
import com.ratefood.app.service.CityService;
import com.ratefood.app.service.ImageService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController()
@RequestMapping("/api")
public class imageMigrations {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CityRepository cityRepository;


    @Autowired
    private ImageService imageService;

    // Maps old Long IDs to new UUIDs for reference (can be saved externally)
    private Map<Long, UUID> restaurantIdMap = new HashMap<>();
    private Map<Long, UUID> dishIdMap = new HashMap<>();
    private Map<Long, UUID> cityIdMap = new HashMap<>();

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
//
//    @PostMapping("/migrate-uuid")
//    @Transactional
//    public void UUIDMigration(){
//
//        List <City> cities = cityRepository.findAll();
//        for(City city:cities){
//            UUID newId = UUID.randomUUID();
//            city.setNewId(newId);
//            cityIdMap.put(city.getId(), newId);
//        }
//        cityRepository.saveAll(cities);
//
//        List<Restaurant> restaurants = restaurantRepository.findAll();
//
//        for (Restaurant r : restaurants) {
//            UUID newId = UUID.randomUUID();
//            restaurantIdMap.put(r.getId(), newId);
//            City city = r.getCity();
//            r.setNewId(newId); // add a UUID column in entity to hold new id
//        }
//        restaurantRepository.saveAll(restaurants);
//
//        List<Dish> dishes = dishRepository.findAll();
//
//        for (Dish d : dishes) {
//            UUID newDishId = UUID.randomUUID();
//            dishIdMap.put(d.getId(), newDishId);
//            d.setNewId(newDishId); // new UUID column for dish id
//
//            // Set new restaurant UUID reference for the dish
//            Long oldRestId = d.getRestaurant().getId();
//            UUID newRestUuid = restaurantIdMap.get(oldRestId);
//            d.setRestaurantUuid(newRestUuid); // new UUID foreign key field in dish
//        }
//        dishRepository.saveAll(dishes);
//
//        System.out.println("Restaurant ID mapping (old -> new UUID):");
//        restaurantIdMap.forEach((oldId, newUuid) -> System.out.println(oldId + " -> " + newUuid));
//
//        System.out.println("Dish ID mapping (old -> new UUID):");
//        dishIdMap.forEach((oldId, newUuid) -> System.out.println(oldId + " -> " + newUuid));
//    }

}
