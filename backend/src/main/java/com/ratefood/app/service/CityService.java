package com.ratefood.app.service;

import com.ratefood.app.dto.response.PageResponseDTO;
import com.ratefood.app.entity.City;
import com.ratefood.app.repository.CityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public PageResponseDTO<List<String>> getCities(String name, Pageable pageable){
        Page<City> page;
        if(name == null || name.isBlank())
            page = cityRepository.findAll(pageable);
        else
            page = cityRepository.findByNameWordPrefix(name, pageable);
        List<String> cityNames = page.getContent()
                .stream()
                .map(City::getName)
                .collect(Collectors.toList());

        PageResponseDTO<List<String>> response = new PageResponseDTO<>();
        response.setData(cityNames);
        response.setCurrentPage(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements((int) page.getTotalElements());
        return response;
    }
}
