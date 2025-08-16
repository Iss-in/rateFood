package com.ratefood.app.dto.request;

import com.ratefood.app.entity.Restaurant;
import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Data
public class DishRequestDTO {

    private String name;

//    private int upvote;
//
//    private int downvote;
//
//    private float rating;

    private String restaurant;

    private List<String> tags;

    private String description;

    private String image;
}
