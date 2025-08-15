package com.ratefood.app.controller;

import com.ratefood.app.entity.City;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.repository.CityRepository;
import com.ratefood.app.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/api")
public class CityController {

    @Autowired
    private CityRepository cityRepository;

    @GetMapping("/city")
    public ResponseEntity<List<String>> getCities() {
        List<String> cityNames = cityRepository.findAll()
                .stream()
                .map(City::getName)
//                .limit(30)
                .collect(Collectors.toList());
//        cityNames.set(7, "Kanpur");
        return new ResponseEntity<>(cityNames, HttpStatus.OK);
    }

}
