package com.examples.springbootmonolithicstarter.domains.product.dto.response;

import com.examples.springbootmonolithicstarter.domains.product.model.Product;
import com.examples.springbootmonolithicstarter.domains.product.model.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        ProductStatus status,
        boolean available,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice().getAmount(),
                product.getStockQuantity(),
                product.getStatus(),
                product.isAvailable(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
