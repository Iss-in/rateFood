package com.ratefood.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "dish")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @NotNull
    @Column(name="name")
    private String name;

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

    private List<String> tags;


}
