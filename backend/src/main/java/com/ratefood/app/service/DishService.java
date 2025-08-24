package com.ratefood.app.service;

import com.ratefood.app.converter.DishConverter;
import com.ratefood.app.dto.request.DishRequestDTO;
import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.DraftDish;
import com.ratefood.app.entity.FavouriteDish;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.enums.ImageType;
import com.ratefood.app.repository.DishRepository;
import com.ratefood.app.repository.DraftDishRepository;
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
import java.util.Optional;
import java.util.UUID;
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

    @Autowired
    private DraftDishRepository draftDishRepository;

    @Autowired
    private ImageService imageService;

    public DishResponseDTO createDish(DishRequestDTO dto, UUID userId) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        String restaurantName = dto.getRestaurant();
        Restaurant restaurant =  restaurantRepository.findByName(restaurantName).orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        if (dto.getImage() == null || dto.getImage().isBlank())
            dto.setImage(imageService.getPlaceholder(ImageType.DISH ));

        if(isAdmin) {
            Dish dishEntity = Dish.builder()
                    .id(UUID.randomUUID())
                    .name(dto.getName())
                    .restaurant(restaurant)
                    .tags(dto.getTags())
                    .image(dto.getImage())
                    .description(dto.getDescription())
                    .build();
            Dish dishCreated = dishRepository.save(dishEntity);


            if (!dto.getImage().contains("foodapp/" + ImageType.DISH)) {
                //TODO: can compress image to a size ?
                String key = ImageType.DISH + "/" + dishCreated.getId();
                imageService.uploadImage(dto.getImage(), key);
                String imageLink = imageService.getPresignedUrl(key, 10);
                dishCreated.setImage(imageLink);
            }

            Dish dishCreatedWithImage = dishRepository.save(dishCreated);
            DishResponseDTO responseDto = dishConverter.fromDishtoDishResponseDTO(dishCreatedWithImage);
            return responseDto;
        }
        else{
            DraftDish dishEntity = DraftDish.builder()
                    .id(UUID.randomUUID())
                    .name(dto.getName())
                    .restaurant(restaurant)
                    .tags(dto.getTags())
                    .description(dto.getDescription())
                    .userId(userId)
                    .build();
            if (!dto.getImage().contains("foodapp/" + ImageType.DRAFT_DISH)) {
                //TODO: can compress image to a size ?
                String key = ImageType.DRAFT_DISH + "/" + dishEntity.getId();
                imageService.uploadImage(dto.getImage(), key);
                String imageLink = imageService.getPresignedUrl(key, 10);
                dishEntity.setImage(imageLink);
            }
            else
                dishEntity.setImage(dto.getImage());
            DraftDish dishCreated  = draftDishRepository.save(dishEntity);
            DishResponseDTO responseDto = dishConverter.fromDraftDishtoDishResponseDTO(dishCreated);
            return responseDto;
        }
    }

    public DishResponseDTO updateDish(DishRequestDTO dto, UUID userId) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        Dish dish = dishRepository.getReferenceById(dto.getId());
        String restaurantName = dto.getRestaurant();
        Restaurant restaurant =  restaurantRepository.findByName(restaurantName).orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        if(isAdmin) {
            Dish dishEntity = Dish.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .restaurant(restaurant)
                    .tags(dto.getTags())
                    .description(dto.getDescription())
                    .build();
            if (dto.getImage() != null && !dto.getImage().contains("foodapp/" + ImageType.DISH)) {
                //TODO: can compress image to a size ?
                String key = ImageType.DISH + "/" + dishEntity.getId();
                imageService.uploadImage(dto.getImage(), key);
                String imageLink = imageService.getPresignedUrl(key, 10);
                dishEntity.setImage(imageLink);
            }
            Dish dishCreated  = dishRepository.save(dishEntity);
            DishResponseDTO responseDto = dishConverter.fromDishtoDishResponseDTO(dishCreated);
            return responseDto;
        }
        else{
            DraftDish dishEntity = DraftDish.builder()
                    .id(UUID.randomUUID())
                    .name(dto.getName())
                    .restaurant(restaurant)
                    .tags(dto.getTags())
                    .description(dto.getDescription())
                    .userId(userId)
                    .dish(dish)
                    .build();
            if (!dto.getImage().contains("foodapp/" + ImageType.DRAFT_DISH)) {
                //TODO: can compress image to a size ?
                String key = ImageType.DRAFT_DISH + "/" + dishEntity.getId();
                imageService.uploadImage(dto.getImage(), key);
                String imageLink = imageService.getPresignedUrl(key, 10);
                dishEntity.setImage(imageLink);
            }
            else
                dishEntity.setImage(dto.getImage());
            DraftDish dishCreated  = draftDishRepository.save(dishEntity);
            DishResponseDTO responseDto = dishConverter.fromDraftDishtoDishResponseDTO(dishCreated);
            return responseDto;
        }
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
            UUID userId
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        Page<Dish> dishes;
        if(isAdmin)
            dishes = dishRepository.getDishes(name, city, minRating, maxRating, currentLatitude,
                    currentLongitude, maxDistanceKm, userId, pageable);
        else
            dishes = dishRepository.getNonDraftDishes(name, city, minRating, maxRating, currentLatitude,
                    currentLongitude, maxDistanceKm, userId, pageable);

        PageResponseDTO<List<DishResponseDTO>> dto = new PageResponseDTO<>();
        dto.setData(dishes.getContent().stream().map(dish -> {
            try {
                return dishConverter.fromDishtoDishResponseDTO(dish, userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        dto.setTotalPages(dishes.getTotalPages());
        dto.setTotalElements((int) dishes.getTotalElements());
        dto.setCurrentPage(dishes.getNumber());
        return dto;
    }

    public PageResponseDTO<List<DishResponseDTO>> getFavouriteDishes(
            String city,
            Pageable pageable,
            UUID userId
    ) {
//        Page<Dish> dishes = dishRepository.getDishesByCIty(city, pageable);
        Page<Dish> dishes = dishRepository.findAll( pageable);

        PageResponseDTO<List<DishResponseDTO>> dto = new PageResponseDTO<>();
        List<DishResponseDTO> dtoList = dishes.getContent().stream().map(dish -> {
            try {
                return dishConverter.fromDishtoDishResponseDTO(dish, userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        dto.setData(dtoList.stream().filter(dish -> dish.getIsFavourite() != null && dish.getIsFavourite()).collect(Collectors.toList()));
        dto.setTotalPages(dishes.getTotalPages());
        dto.setTotalElements((int) dishes.getTotalElements());
        dto.setCurrentPage(dishes.getNumber());
        return dto;
    }

    public Boolean markDishFavourite(UUID dishId , UUID userId){
        Optional<FavouriteDish> existingFavouriteDish = favouriteDishRepository.getFavouriteDishesByUserIdAndDishId(userId, dishId);
        if(existingFavouriteDish.isPresent())
            return Boolean.FALSE;

        FavouriteDish favouriteDish = favouriteDishRepository.getFavouriteDishesByUserIdAndDishId(userId, dishId).orElseGet(() -> {
            FavouriteDish newFavourite = FavouriteDish.builder()
                    .dish(dishRepository.findById(dishId).orElseThrow(() -> new EntityNotFoundException("Dish not found")))
                    .userId(userId)
                    .build();
            return newFavourite;
        });
        favouriteDishRepository.save(favouriteDish);
        Dish dish = dishRepository.findById(dishId).orElseThrow(() -> new EntityNotFoundException("Dish not found"));

        dish.setFavoriteCount(dish.getFavoriteCount() + 1);
        dishRepository.save(dish);
        return Boolean.TRUE;
    }

    public Boolean unMarkDishFavourite(UUID dishId , UUID userId){
        FavouriteDish favouriteDish = favouriteDishRepository.getFavouriteDishesByUserIdAndDishId(userId, dishId).
                orElseThrow(() -> new EntityNotFoundException("Dish not found"));
        favouriteDishRepository.delete(favouriteDish);

        Dish dish = dishRepository.findById(dishId).orElseThrow(() -> new EntityNotFoundException("Dish not found"));
        dish.setFavoriteCount(Math.max(0, dish.getFavoriteCount() - 1));
        dishRepository.save(dish);
        return Boolean.TRUE;
    }

    public List<DishResponseDTO> getDraftDishes(UUID userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<DraftDish> draftDishes;
        if(isAdmin)
            draftDishes =  draftDishRepository.findAll();
        else
            draftDishes = draftDishRepository.findByUserId(userId);

        List<DishResponseDTO> dto = draftDishes.stream().map(draftDish -> {
            try {
                return dishConverter.
                        fromDraftDishtoDishResponseDTO(draftDish);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return dto;
    }

    public Boolean approveDraftDish(UUID dishId, UUID userId) throws Exception {
        DraftDish draftDish = draftDishRepository.findById(dishId).orElseThrow(() -> new EntityNotFoundException("Draft dish not found"));
//        if(draftDish.getUserId() != userId){
//            throw new RuntimeException("You can only approve your own draft dishes");
//        }
        Dish dish; UUID id;
        if(draftDish.getDish() == null) {
            id = UUID.randomUUID();
            dish = Dish.builder()
                    .id(id)
                    .name(draftDish.getName())
                    .restaurant(draftDish.getRestaurant())
                    .tags(draftDish.getTags())
                    .description(draftDish.getDescription())
                    .build();
        }
        else{
            id = draftDish.getDish().getId();
            dish = draftDish.getDish();
            dish.setName(draftDish.getName());
            dish.setTags(draftDish.getTags());
            dish.setDescription(draftDish.getDescription());
            dish.setRestaurant(draftDish.getRestaurant());
        }
        String draftImageKey = ImageType.DRAFT_DISH + "/" + dishId ;
        String imageKey = ImageType.DISH + "/" + id ;
        dish.setImage(imageService.replaceImage(draftImageKey, imageKey));
        dishRepository.save(dish);
        draftDishRepository.delete(draftDish);
        return Boolean.TRUE;
    }
}
