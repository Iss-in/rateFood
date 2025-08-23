package com.ratefood.app.repository;

import com.ratefood.app.entity.DraftRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DraftRestaurantRepository extends JpaRepository<DraftRestaurant, UUID> {
    List<DraftRestaurant> findByUserId(UUID userId);
    Optional<DraftRestaurant> findByIdAndUserId(UUID id, UUID userId);

}

