package com.ratefood.app.repository;

import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.Restaurant;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//
public interface DishRepository extends JpaRepository<Dish, Long> {

    @Query("""
  SELECT d FROM Dish d
  JOIN d.restaurant r
  JOIN r.city c
  WHERE (:name IS NULL OR :name = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
    AND LOWER(c.name) = LOWER(:city)

""")
    Page<Dish> getDishes(
            @Param("name") String name,
            @Param("city") String city,
            @Param("minRating") Float minRating,
            @Param("maxRating") Float maxRating,
            @Param("currentLatitude") Double currentLatitude,
            @Param("currentLongitude") Double currentLongitude,
            @Param("maxDistanceKm") Double maxDistanceKm,
            @Param("userId") Long userId,
            Pageable pageable
    );



    @Query("""
      SELECT d FROM Dish d
      JOIN d.restaurant r
      JOIN r.city c
      WHERE (:name IS NULL OR :name = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND LOWER(c.name) = LOWER(:city)
        AND NOT EXISTS (
            SELECT 1 FROM DraftDish dd
            WHERE dd.userId = :userId
              AND dd.dish IS NOT NULL
              AND dd.dish.id = d.id

        )
    """)
    Page<Dish> getNonDraftDishes(
            @Param("name") String name,
            @Param("city") String city,
            @Param("minRating") Float minRating,
            @Param("maxRating") Float maxRating,
            @Param("currentLatitude") Double currentLatitude,
            @Param("currentLongitude") Double currentLongitude,
            @Param("maxDistanceKm") Double maxDistanceKm,
            @Param("userId") Long userId,
            Pageable pageable
    );


    Page<Dish> findAll(Pageable pageable);
}

