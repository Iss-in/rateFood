package com.ratefood.app.repository;

import com.ratefood.app.entity.FavouriteDish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteDishRepository extends JpaRepository<FavouriteDish, Long> {
    Optional<FavouriteDish> findByUserId(Long userId);

    Optional<FavouriteDish> getFavouriteDishesByUserIdAndDishId(Long userId, long dishId);

    List<FavouriteDish> getFavouriteDishesByUserId(Long userId);
}
