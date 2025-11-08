package com.example.demo.controller;



import com.example.demo.entity.Bread;
import com.example.demo.service.BreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breads")
@CrossOrigin(origins = "http://localhost:3000") // Для React-приложения
public class BreadController {

    @Autowired
    private BreadService breadService;

    @GetMapping
    public List<Bread> getAllBreads() {
        return breadService.getAllBreads();
    }

    @GetMapping("/{id}")
    public Bread getBreadById(@PathVariable Long id) {
        return breadService.getBreadById(id);
    }
}