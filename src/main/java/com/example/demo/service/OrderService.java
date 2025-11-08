package com.example.demo.service;


import com.example.demo.entity.*;
import com.example.demo.repository.BreadRepository;
import com.example.demo.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BreadRepository breadRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setCustomerName(orderRequest.getCustomerName());
        order.setCustomerPhone(orderRequest.getCustomerPhone());
        order.setDeliveryAddress(orderRequest.getDeliveryAddress());

        double totalAmount = 0;

        for (OrderRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {
            Bread bread = breadRepository.findById(itemRequest.getBreadId())
                    .orElseThrow(() -> new RuntimeException("Bread not found with id: " + itemRequest.getBreadId()));

            OrderItem orderItem = new OrderItem(order, bread, itemRequest.getQuantity());
            order.getItems().add(orderItem);

            totalAmount += bread.getPrice() * itemRequest.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return convertToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return convertToOrderResponse(order);
    }

    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToOrderResponse(updatedOrder);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setCustomerName(order.getCustomerName());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setOrderDate(order.getOrderDate());
        orderResponse.setTotalAmount(order.getTotalAmount());

        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream().map(orderItem -> {
            OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
            itemResponse.setBreadName(orderItem.getBread().getName());
            itemResponse.setQuantity(orderItem.getQuantity());
            itemResponse.setPrice(orderItem.getBread().getPrice());
            itemResponse.setSubtotal(orderItem.getBread().getPrice() * orderItem.getQuantity());
            return itemResponse;
        }).collect(Collectors.toList());

        orderResponse.setItems(itemResponses);
        return orderResponse;
    }
}
