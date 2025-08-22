package com.ratefood.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "draft_dish")
public class DraftDish  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="image")
    @Builder.Default
    private String image = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=1160&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";

    @NotNull
    @Column(name="name")
    private String name;

    @NotNull
    @Column(name="description")
    private String description;

    @Column(name="upvote")
    private int upvote;

    @Column(name="downvote")
    private int downvote;

    @Column(name="rating")
    private float rating;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name="tags")
    private List<String> tags;

    @Column(name="user_id")
    private Long userId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "dish_id", nullable = true)
    private Dish dish ;
}
