package com.ratefood.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name="name")
    public String name;

//    @Column(name="pincode")
//    public List<Integer> pincode;

//    @OneToMany(mappedBy = "city")
//    private List<Restaurant> restaurants;

}
