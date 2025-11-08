package com.example.demo.entity;


import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long orderId;
    private String customerName;
    private String status;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private List<OrderItemResponse> items;

    // Конструкторы, геттеры и сеттеры
    public OrderResponse() {}

    // Геттеры и сеттеры
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    public static class OrderItemResponse {
        private String breadName;
        private Integer quantity;
        private Double price;
        private Double subtotal;

        // Геттеры и сеттеры
        public String getBreadName() { return breadName; }
        public void setBreadName(String breadName) { this.breadName = breadName; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    }
}