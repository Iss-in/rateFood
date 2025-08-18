package com.ratefood.app.controller;

import com.ratefood.app.dto.response.DishResponseDTO;
import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.City;
import com.ratefood.app.entity.Dish;
import com.ratefood.app.repository.CityRepository;
import com.ratefood.app.repository.DishRepository;
import com.ratefood.app.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @Autowired
    private CityService cityService;

    @GetMapping("/city")
    public PageResponseDTO<List<String>> getCities(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PageableDefault Pageable pageable
            ) {
        PageResponseDTO<List<String>> cities = cityService.getCities(name, pageable);
        return cities;
    }

}
