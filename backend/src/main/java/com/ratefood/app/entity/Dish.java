package com.ratefood.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dish")
public class Dish {

    @Id
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(
//            name = "UUID",
//            strategy = "org.hibernate.id.UUIDGenerator"
//    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @NotNull
    @Column(name="name")
    private String name;

    @NotNull
    @Column(name="description")
    private String description;


//    @Column(name="cuisine")
//    private String cuisine;
//
//    @Column(name="place") // use it as secondary key
//    private String place;

    @Column(name="upvote")
    private int upvote;

    @Column(name="downvote")
    private int downvote;

    @Column(name="rating")
    private float rating;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "restaurant_uuid")
    private UUID restaurantUuid ;

    @Column(name="tags")
    private List<String> tags;
//
//    @Builder.Default
//    @Column(name="is_draft")
//    private boolean isDraft = false;

    @Column
    @Builder.Default
    private int favoriteCount = 0;
}
