package com.ratefood.app.controller;

import com.ratefood.app.dto.request.DishRequestDTO;
import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.DraftDish;
import com.ratefood.app.entity.Restaurant;
import com.ratefood.app.enums.ImageType;
import com.ratefood.app.repository.DishRepository;
import com.ratefood.app.repository.DraftDishRepository;
import com.ratefood.app.repository.RestaurantRepository;
import com.ratefood.app.service.DishService;
import com.ratefood.app.service.ImageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class DishController {
//    private static final Logger log = LoggerFactory.getLogger(DishController.class);
    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private DishService dishService;

    @Autowired
    private DraftDishRepository draftDishRepository;

    @Autowired
    private ImageService imageService;


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/dish")
    public ResponseEntity<DishResponseDTO> addDish(
            @RequestBody DishRequestDTO dishDTO,
            @RequestHeader("X-User-Id") UUID userId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authenticated user authorities: {}", authentication.getAuthorities());
        DishResponseDTO newDish = dishService.createDish(dishDTO, userId);
        return new ResponseEntity<>(newDish, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/dish/favourite/{dishId}")
    public ResponseEntity<Boolean> markDishFavourite(
            @PathVariable UUID dishId, @RequestHeader("X-User-Id") UUID userId) {
        return new ResponseEntity<>(dishService.markDishFavourite(dishId, userId), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/dish/unFavourite/{dishId}")
    public ResponseEntity<Boolean> markDishUnFavourite(
            @PathVariable UUID dishId, @RequestHeader("X-User-Id") UUID userId) {
        return new ResponseEntity<>(dishService.unMarkDishFavourite(dishId, userId), HttpStatus.CREATED);
    }


    //    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dish/{city}")
    public PageResponseDTO<List<DishResponseDTO>> getdishes(
            @PathVariable String city,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Float minRating,
            @RequestParam(required = false) Float maxRating,
            @RequestParam(required = false) Double currentLatitude,
            @RequestParam(required = false) Double currentLongitude,
            @RequestParam(required = false) Double maxDistanceKm,
            @RequestParam(required = false) Boolean favourites,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PageableDefault Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authenticated user authorities: {}", authentication.getAuthorities());

        PageResponseDTO<List<DishResponseDTO>> dishes = dishService.getDishes(name, city, minRating, maxRating,
                currentLatitude, currentLongitude, maxDistanceKm, favourites, pageable, userId);
        return dishes;
//        return new ResponseEntity<>(restaurants, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/dish/favourites/{city}")
    public PageResponseDTO<List<DishResponseDTO>> getFavouritedishes(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PageableDefault Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("User with roles: {} is getting dishes.", authentication.getAuthorities());
        PageResponseDTO<List<DishResponseDTO>> dishes = dishService.getFavouriteDishes(city, pageable, userId);
        return dishes;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/updateDish")
    public ResponseEntity<DishResponseDTO> updateDish(
            @RequestBody DishRequestDTO dishDTO, @RequestHeader("X-User-Id") UUID userId) throws Exception {
        DishResponseDTO newDish = dishService.updateDish(dishDTO, userId);
        return new ResponseEntity<>(newDish, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/dish/{dishId}")
    public ResponseEntity deleteDish(@PathVariable UUID dishId) throws Exception {
        Dish dish = dishRepository.getReferenceById(dishId);
        imageService.deleteByKey(ImageType.DISH + "/" + dishId);
        dishRepository.delete(dish);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/dish/draft")
    public ResponseEntity<List<DishResponseDTO>> getDraftDishes(
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authenticated user authorities: {}", authentication.getAuthorities());

        List<DishResponseDTO> dishes = dishService.getDraftDishes(userId);
        return new ResponseEntity<>(dishes, HttpStatus.ACCEPTED);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/dish/draft/{id}")
    public ResponseEntity deleteDraftDish(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        DraftDish dish;
        if(isAdmin)
            dish = draftDishRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Draft dish not found"));
        else
            dish = draftDishRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new EntityNotFoundException("Draft dish not found"));

        draftDishRepository.delete(dish);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/dish/draft/{id}")
    public ResponseEntity approveDraftDish(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        log.info("Authenticated user authorities: {}", authentication.getAuthorities());
        log.info("Authenticated user authorities: {}");

        dishService.approveDraftDish(id, userId);
        return new ResponseEntity<>(false, HttpStatus.CREATED);
    }

}
