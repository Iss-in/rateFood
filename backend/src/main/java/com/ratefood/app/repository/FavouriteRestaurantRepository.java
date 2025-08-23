package com.ratefood.app.repository;

import com.ratefood.app.entity.FavouriteDish;
import com.ratefood.app.entity.FavouriteRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavouriteRestaurantRepository extends JpaRepository<FavouriteRestaurant, Long> {
    Optional<FavouriteRestaurant> findByUserId(UUID userId);

    Optional<FavouriteRestaurant> getFavouriteRestaurantByUserIdAndRestaurantId(UUID userId, UUID restaurantId);
    List<FavouriteRestaurant> getFavouriteRestaurantsByUserId(UUID userId);
}
