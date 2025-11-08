package com.example.demo.entity;


import java.util.List;

public class OrderRequest {
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private List<OrderItemRequest> items;

    // Конструкторы, геттеры и сеттеры
    public OrderRequest() {}

    // Геттеры и сеттеры
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public static class OrderItemRequest {
        private Long breadId;
        private Integer quantity;

        // Геттеры и сеттеры
        public Long getBreadId() { return breadId; }
        public void setBreadId(Long breadId) { this.breadId = breadId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}