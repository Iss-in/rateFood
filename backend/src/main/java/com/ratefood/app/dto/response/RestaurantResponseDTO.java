package com.ratefood.app.dto.response;

import com.ratefood.app.entity.City;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseDTO {

    private UUID id;

    private String name;

    private String cuisine;

    private String description;

    private float rating;

    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private String image = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop";

    private BigDecimal latitude = BigDecimal.valueOf(28.6139);

    private BigDecimal longitude = BigDecimal.valueOf(28.6139);

    private City city;

    @Builder.Default
    Boolean isFavourite = false;

    @Builder.Default
    private int favoriteCount = 0;
}
