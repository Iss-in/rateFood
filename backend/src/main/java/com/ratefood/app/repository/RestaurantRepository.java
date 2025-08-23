package com.ratefood.app.repository;

import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.Restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> , JpaSpecificationExecutor<Restaurant> {

    @Query(value = """
    SELECT r FROM Restaurant r
    WHERE (:name IS NULL  OR :name = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND ( LOWER(r.city.name) = LOWER(:city) )
""")
    Page<Restaurant> getRestaurants(
            @Param("name") String name,
            @Param("city") String city,
            @Param("minRating") Float minRating,
            @Param("maxRating") Float maxRating,
            @Param("currentLatitude") Double currentLatitude,
            @Param("currentLongitude") Double currentLongitude,
            @Param("maxDistanceKm") Double maxDistanceKm,
            UUID userId,
            Pageable pageable
    );



    @Query("""
      SELECT r from Restaurant r
      JOIN r.city c
      WHERE (:name IS NULL OR :name = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND LOWER(c.name) = LOWER(:city)
        AND NOT EXISTS (
            SELECT 1 FROM DraftRestaurant dr
            WHERE dr.userId = :userId
              AND dr.restaurant IS NOT NULL
              AND dr.restaurant.id = r.id

        )
    """)
    Page<Restaurant> getNonDraftDishes(
            @Param("name") String name,
            @Param("city") String city,
            @Param("minRating") Float minRating,
            @Param("maxRating") Float maxRating,
            @Param("currentLatitude") Double currentLatitude,
            @Param("currentLongitude") Double currentLongitude,
            @Param("maxDistanceKm") Double maxDistanceKm,
            @Param("userId") UUID userId,
            Pageable pageable
    );

    @Query("""
  SELECT r FROM Restaurant r
  JOIN r.city c
  WHERE  LOWER(c.name) = LOWER(:city)

""")
    Page<Restaurant> getRestaurantsByCIty(
            @Param("city") String city,
            Pageable pageable
    );


    Optional<Restaurant> findByName(String name);
}
