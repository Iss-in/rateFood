package com.ratefood.app.repository;

import com.ratefood.app.entity.FavouriteDish;
import com.ratefood.app.entity.FavouriteRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRestaurantRepository extends JpaRepository<FavouriteRestaurant, Long> {
    Optional<FavouriteRestaurant> findByUserId(Long userId);

    Optional<FavouriteRestaurant> getFavouriteRestaurantByUserIdAndRestaurantId(Long userId, long restaurantId);
    List<FavouriteRestaurant> getFavouriteRestaurantsByUserId(Long userId);
}
