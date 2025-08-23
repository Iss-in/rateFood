package com.ratefood.app.repository;

import com.ratefood.app.entity.FavouriteDish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavouriteDishRepository extends JpaRepository<FavouriteDish, UUID> {
    Optional<FavouriteDish> findByUserId(UUID userId);

    Optional<FavouriteDish> getFavouriteDishesByUserIdAndDishId(UUID userId, UUID dishId);

    List<FavouriteDish> getFavouriteDishesByUserId(UUID userId);
}
