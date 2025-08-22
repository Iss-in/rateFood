package com.ratefood.app.repository;

import com.ratefood.app.entity.Dish;
import com.ratefood.app.entity.DraftDish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DraftDishRepository extends JpaRepository<DraftDish, Long> {
    List<DraftDish> findByUserId(Long userId);
    Optional<DraftDish> findByIdAndUserId(Long id, Long userId);

}

