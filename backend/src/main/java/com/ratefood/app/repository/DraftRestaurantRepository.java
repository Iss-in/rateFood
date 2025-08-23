package com.ratefood.app.repository;

import com.ratefood.app.entity.DraftRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DraftRestaurantRepository extends JpaRepository<DraftRestaurant, Long> {
    List<DraftRestaurant> findByUserId(Long userId);
    Optional<DraftRestaurant> findByIdAndUserId(Long id, Long userId);

}

