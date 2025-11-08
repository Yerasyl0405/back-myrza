package com.example.demo.service;


import com.example.demo.entity.Bread;
import com.example.demo.repository.BreadRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BreadService {

    @Autowired
    private BreadRepository breadRepository;

    public List<Bread> getAllBreads() {
        return breadRepository.findAll();
    }

    public Bread getBreadById(Long id) {
        return breadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bread not found with id: " + id));
    }

    // Инициализация базы данных с 7 видами хлеба
    @PostConstruct
    public void init() {
        if (breadRepository.count() == 0) {
            breadRepository.save(new Bread("Белый хлеб", "Пшеничный хлеб", 50.0, "/images/white.jpg"));
            breadRepository.save(new Bread("Ржаной хлеб", "Традиционный ржаной хлеб", 60.0, "/images/rye.jpg"));
            breadRepository.save(new Bread("Бородинский", "Ржаной хлеб с тмином и кориандром", 70.0, "/images/borodinsky.jpg"));
            breadRepository.save(new Bread("Цельнозерновой", "Полезный хлеб из цельного зерна", 80.0, "/images/wholegrain.jpg"));
            breadRepository.save(new Bread("Багет", "Французский багет", 40.0, "/images/baguette.jpg"));
            breadRepository.save(new Bread("Кукурузный", "Хлеб из кукурузной муки", 75.0, "/images/corn.jpg"));
            breadRepository.save(new Bread("Бездрожжевой", "Хлеб без дрожжей", 65.0, "/images/no_yeast.jpg"));
        }
    }
}