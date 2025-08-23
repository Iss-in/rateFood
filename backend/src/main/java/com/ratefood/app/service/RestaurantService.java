package com.ratefood.app.service;

import com.ratefood.app.converter.RestaurantConverter;
import com.ratefood.app.dto.request.RestaurantRequestDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.dto.response.RestaurantResponseDTO;
import com.ratefood.app.entity.*;
import com.ratefood.app.enums.ImageType;
import com.ratefood.app.repository.CityRepository;
import com.ratefood.app.repository.DraftRestaurantRepository;
import com.ratefood.app.repository.FavouriteRestaurantRepository;
import com.ratefood.app.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
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

    @Autowired
    private DraftRestaurantRepository draftRestaurantRepository;

    public PageResponseDTO<List<RestaurantResponseDTO>> getRestaurants(
            String name,
            String city,
            Float minRating,
            Float maxRating,
            Double currentLatitude,
            Double currentLongitude,
            Double maxDistanceKm,
            Pageable pageable,
            UUID userId
    ){
//        Page<Restaurant> restaurants = restaurantRepository.getRestaurants(name, city, minRating, maxRating, currentLatitude,
//                currentLongitude, maxDistanceKm, pageable);
//
//        PageResponseDTO<List<RestaurantResponseDTO>> dto = new PageResponseDTO<>();
//        dto.setData(restaurants.getContent().stream().map(restaurant -> {
//            try {
//                return restaurantConverter.fromRestaurantToRestaurantResponseDTO(restaurant, userId);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }).collect(java.util.stream.Collectors.toList()));
//        dto.setTotalPages(restaurants.getTotalPages());
//        dto.setTotalElements((int) restaurants.getTotalElements());
//        dto.setCurrentPage(restaurants.getNumber());
//        return dto;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        Page<Restaurant> restaurants;
        if(isAdmin)
            restaurants = restaurantRepository.getRestaurants(name, city, minRating, maxRating, currentLatitude,
                    currentLongitude, maxDistanceKm, userId, pageable);
        else
            restaurants = restaurantRepository.getNonDraftDishes(name, city, minRating, maxRating, currentLatitude,
                    currentLongitude, maxDistanceKm, userId, pageable);

        PageResponseDTO<List<RestaurantResponseDTO>> dto = new PageResponseDTO<>();
        dto.setData(restaurants.getContent().stream().map(restaurant -> {
            try {
                return restaurantConverter.fromRestaurantToRestaurantResponseDTO(restaurant, userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        dto.setTotalPages(restaurants.getTotalPages());
        dto.setTotalElements((int) restaurants.getTotalElements());
        dto.setCurrentPage(restaurants.getNumber());
        return dto;
    }

    public RestaurantResponseDTO addRestaurant(RestaurantRequestDTO restaurantDTO, UUID userId) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        String cityName = restaurantDTO.getCity();
        City city = cityRepository.findByName(cityName.toLowerCase()).orElseThrow(() -> new EntityNotFoundException("City not found"));

        if (restaurantDTO.getImage() == null || restaurantDTO.getImage().isBlank())
            restaurantDTO.setImage(imageService.getPlaceholder(ImageType.RESTAURANT ));

        if(isAdmin) {
            Restaurant restaurantEntity = Restaurant.builder()
                    .name(restaurantDTO.getName())
                    .cuisine(restaurantDTO.getCuisine())
                    .description(restaurantDTO.getDescription())
                    .tags(restaurantDTO.getTags())
                    .image(restaurantDTO.getImage())
                    .city(city)
                    .build();
            Restaurant restaurantCreated = restaurantRepository.save(restaurantEntity);
                if (!restaurantDTO.getImage().contains("foodapp/" + ImageType.RESTAURANT)) {
                //TODO: can compress image to a size ?
                String key = ImageType.RESTAURANT + "/" + String.valueOf(restaurantCreated.getId()) ;
                imageService.uploadImage(restaurantDTO.getImage(), key);
                String imageLink = imageService.getPresignedUrl(key, 10);
                restaurantCreated.setImage(imageLink);
            }

            return restaurantConverter.fromRestaurantToRestaurantResponseDTO(restaurantRepository.save(restaurantCreated));
        }
        else{
            DraftRestaurant draftRestaurant = DraftRestaurant.builder()
                    .id(UUID.randomUUID())
                    .name(restaurantDTO.getName())
                    .city(city)
                    .tags(restaurantDTO.getTags())
                    .description(restaurantDTO.getDescription())
                    .userId(userId)
                    .build();
            if (!restaurantDTO.getImage().contains("foodapp/" + ImageType.DRAFT_RESTAURANT)) {
                String key = ImageType.DRAFT_RESTAURANT + "/" + String.valueOf(draftRestaurant.getId());
                imageService.uploadImage(restaurantDTO.getImage(), key);
                String imageLink = imageService.getPresignedUrl(key, 10);
                draftRestaurant.setImage(imageLink);
            }
            else
                draftRestaurant.setImage(restaurantDTO.getImage());
            DraftRestaurant restaurantCreated  = draftRestaurantRepository.save(draftRestaurant);
            RestaurantResponseDTO responseDto = restaurantConverter.fromDraftRestaurantToRestaurantResponseDTO(restaurantCreated);
            return responseDto;
        }
    }

    public RestaurantResponseDTO updateRestaurant(RestaurantRequestDTO restaurantDTO, UUID userId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        String cityName = restaurantDTO.getCity();
        City city = cityRepository.findByName(cityName).orElseThrow(() -> new EntityNotFoundException("City not found"));


        if(isAdmin) {
            Restaurant restaurantEntity = Restaurant.builder()
                    .id(restaurantDTO.getId())
                    .name(restaurantDTO.getName())
                    .cuisine(restaurantDTO.getCuisine())
                    .description(restaurantDTO.getDescription())
                    .tags(restaurantDTO.getTags())
                    .image(restaurantDTO.getImage())
                    .city(city)
                    .build();
            if (!restaurantDTO.getImage().contains("foodapp/" + ImageType.RESTAURANT)) {
                String key = ImageType.RESTAURANT + "/" + String.valueOf(restaurantEntity.getId()) ;
                imageService.uploadImage(restaurantDTO.getImage(), key);
                String imageLink = imageService.getPresignedUrl(key, 10);
                restaurantEntity.setImage(imageLink);
            }
            else
                restaurantEntity.setImage(restaurantDTO.getImage());
            Restaurant restaurantCreated = restaurantRepository.save(restaurantEntity);
            return restaurantConverter.fromRestaurantToRestaurantResponseDTO(restaurantCreated);
        }
        else{
            DraftRestaurant draftRestaurant = DraftRestaurant.builder()
                    .id(UUID.randomUUID())
                    .name(restaurantDTO.getName())
                    .city(city)
                    .tags(restaurantDTO.getTags())
                    .cuisine(restaurantDTO.getCuisine())
                    .description(restaurantDTO.getDescription())
                    .restaurant(restaurantRepository.findById(restaurantDTO.getId()).orElseThrow(() -> new EntityNotFoundException("Restaurant not found")))
                    .userId(userId)
                    .build();
            if (!restaurantDTO.getImage().contains("foodapp/" + ImageType.DRAFT_RESTAURANT)) {
                String key = ImageType.DRAFT_RESTAURANT + "/" + String.valueOf(restaurantDTO.getId());
                imageService.uploadImage(restaurantDTO.getImage(), key);
                String imageLink = imageService.getPresignedUrl(key, 10);
                draftRestaurant.setImage(imageLink);
            }
            else
                draftRestaurant.setImage(restaurantDTO.getImage());
            DraftRestaurant draftRestaurantCreated = draftRestaurantRepository.save(draftRestaurant);
            RestaurantResponseDTO responseDto = restaurantConverter.fromDraftRestaurantToRestaurantResponseDTO(draftRestaurantCreated);
            return responseDto;
        }
    }

    public PageResponseDTO<List<RestaurantResponseDTO>> getFavouriteRestaurants(
            String city,
            Pageable pageable,
            UUID userId
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


    @Transactional
    public Boolean markRestaurantFavourite(UUID restaurantId , UUID userId){


        FavouriteRestaurant favouriteRestaurant = favouriteRestaurantRepository.
                getFavouriteRestaurantByUserIdAndRestaurantId(userId, restaurantId).orElseGet(() -> {
                    FavouriteRestaurant newFavouriteRestaurant = FavouriteRestaurant.builder()
                            .restaurant(restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Dish not found")))
                            .userId(userId)
                            .build();
                    return newFavouriteRestaurant;
                });
        favouriteRestaurantRepository.save(favouriteRestaurant);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        restaurant.setFavoriteCount(restaurant.getFavoriteCount() + 1);
        restaurantRepository.save(restaurant);
        return Boolean.TRUE;
   }

    public Boolean unMarkRestaurantFavourite(UUID restaurantId , UUID userId){
        FavouriteRestaurant favouriteRestaurant = favouriteRestaurantRepository.getFavouriteRestaurantByUserIdAndRestaurantId(userId, restaurantId).
                orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        favouriteRestaurantRepository.delete(favouriteRestaurant);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        restaurant.setFavoriteCount(Math.max(0, restaurant.getFavoriteCount() - 1));
        restaurantRepository.save(restaurant);
        return Boolean.TRUE;
    }

    public List<RestaurantResponseDTO> getDraftRestaurants(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<DraftRestaurant> draftRestaurants;
        if(isAdmin)
            draftRestaurants =  draftRestaurantRepository.findAll();
        else
            draftRestaurants = draftRestaurantRepository.findByUserId(userId);
        return draftRestaurants.stream().map(draftRestaurant -> {
            try {
                return restaurantConverter.fromDraftRestaurantToRestaurantResponseDTO(draftRestaurant);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public Boolean approveDraftRestaurant(UUID restaurantId, UUID userId) throws Exception {
        DraftRestaurant draftRestaurant = draftRestaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        Restaurant restaurant;
        UUID id;
        if (draftRestaurant.getRestaurant() == null) {
            id = UUID.randomUUID();
            restaurant = Restaurant.builder()
                    .id(id)
                    .name(draftRestaurant.getName())
                    .city(draftRestaurant.getCity())
                    .description(draftRestaurant.getDescription())
                    .tags(draftRestaurant.getTags())
                    .cuisine(draftRestaurant.getCuisine())
                    .build();
        } else {
            id = draftRestaurant.getRestaurant().getId();
            restaurant = draftRestaurant.getRestaurant();
            restaurant.setName(draftRestaurant.getName());
            restaurant.setCity(draftRestaurant.getCity());
            restaurant.setDescription(draftRestaurant.getDescription());
            restaurant.setTags(draftRestaurant.getTags());
            restaurant.setCuisine(draftRestaurant.getCuisine());

        }
        String draftImageKey = ImageType.DRAFT_RESTAURANT + "/" + draftRestaurant.getId() ;
        String imageKey = ImageType.RESTAURANT + "/" + id ;
        restaurant.setImage(imageService.replaceImage(draftImageKey, imageKey));
        restaurantRepository.save(restaurant);
        draftRestaurantRepository.delete(draftRestaurant);
        return Boolean.TRUE;
    }
}
