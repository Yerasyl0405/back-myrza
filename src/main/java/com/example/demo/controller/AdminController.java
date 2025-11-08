package com.example.demo.controller;


import com.example.demo.entity.OrderResponse;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")

public class AdminController {

    @Autowired
    private OrderService orderService;

  @GetMapping
  public List<OrderResponse> getOrders(){
      return orderService.getAllOrders();
  }
}
