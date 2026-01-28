package com.examples.springbootmonolithicstarter.domains.product.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price", nullable = false))
    private Money price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Product(String name, String description, Money price, Integer stockQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = ProductStatus.AVAILABLE;
        this.createdAt = LocalDateTime.now();
    }

    public static Product create(String name, String description, BigDecimal price, Integer stockQuantity) {
        return new Product(name, description, Money.of(price), stockQuantity);
    }

    public void updateInfo(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = Money.of(price);
        this.updatedAt = LocalDateTime.now();
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("추가할 재고 수량은 0보다 커야 합니다.");
        }
        this.stockQuantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("차감할 재고 수량은 0보다 커야 합니다.");
        }
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.stockQuantity);
        }
        this.stockQuantity = restStock;
        this.updatedAt = LocalDateTime.now();
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = ProductStatus.AVAILABLE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAvailable() {
        return this.status == ProductStatus.AVAILABLE && this.stockQuantity > 0;
    }

    public Money calculateTotalPrice(int quantity) {
        return this.price.multiply(quantity);
    }
}
