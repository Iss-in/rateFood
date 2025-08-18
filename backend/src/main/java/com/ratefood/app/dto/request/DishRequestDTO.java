package com.ratefood.app.dto.request;

import com.ratefood.app.entity.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Data
@Builder
public class DishRequestDTO {

    private String name;

    private String restaurant;

    private List<String> tags;

    private String description;

    private String image;

    @Builder.Default
    private boolean draft = false; // true for draft, false for published

}
