package com.ratefood.app.repository;

import com.ratefood.app.entity.Restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface RestaurantRepository extends JpaRepository<Restaurant, Long> , JpaSpecificationExecutor<Restaurant> {

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
            Pageable pageable
    );

    Optional<Restaurant> findByName(String name);
}
