package com.example.demo.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "bread_id")
    private Bread bread;

    private Integer quantity;

    // Конструкторы, геттеры и сеттеры
    public OrderItem() {}

    public OrderItem(Order order, Bread bread, Integer quantity) {
        this.order = order;
        this.bread = bread;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Bread getBread() { return bread; }
    public void setBread(Bread bread) { this.bread = bread; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
