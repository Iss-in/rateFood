package com.ratefood.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    public Long id;

    @Column(name="name")
    public String name;

    @Column(name="pincode")
    public List<Integer> pincode;

//    @OneToMany(mappedBy = "city")
//    private List<Restaurant> restaurants;

}
