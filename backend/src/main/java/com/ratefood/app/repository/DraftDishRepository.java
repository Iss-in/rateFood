package com.ratefood.app.repository;

import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.DraftDish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DraftDishRepository extends JpaRepository<DraftDish, UUID> {
    List<DraftDish> findByUserId(UUID userId);
    Optional<DraftDish> findByIdAndUserId(UUID id, UUID userId);

}

