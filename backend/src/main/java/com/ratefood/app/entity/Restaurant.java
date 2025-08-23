package com.ratefood.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(name="name")
    private String name;

    @Column(name="cuisine")
    private String cuisine;

    @Column(name="description")
    private String description;


    @Column(name="rating")
    private float rating;

    @Column(name="tags")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Column(name = "image", columnDefinition = "TEXT")
    @Builder.Default
    private String image = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop";

    @Column(name="latitude")
    @Builder.Default
    private BigDecimal latitude = BigDecimal.valueOf(28.6139);

    @Column
    @Builder.Default
    private BigDecimal longitude = BigDecimal.valueOf(28.6139);


    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(nullable = false)
    @ColumnDefault("0")
    @Builder.Default
    private int favoriteCount = 0;
}
