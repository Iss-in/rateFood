package com.ratefood.app.dto.request;

import com.ratefood.app.entity.City;
import com.ratefood.app.entity.Dish;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class RestaurantRequestDTO {

    public long id;

    private String name;

    private String cuisine;

    private String description;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private String image = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop";

    @Builder.Default
    private BigDecimal latitude = BigDecimal.valueOf(28.6139);

    @Builder.Default
    private BigDecimal longitude = BigDecimal.valueOf(28.6139);

    private String city;


}
