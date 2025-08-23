package com.ratefood.app.dto.response;

import com.ratefood.app.entity.Restaurant;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class DishResponseDTO {
    private UUID id;
    private String name;

    private String restaurant;

    private List<String> tags;

    private String description;

    private String image;

    @Builder.Default
    private Boolean isFavourite = false;

    @Builder.Default
    private int favoriteCount = 0;
}
