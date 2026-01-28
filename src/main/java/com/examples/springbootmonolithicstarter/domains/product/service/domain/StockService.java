package com.examples.springbootmonolithicstarter.domains.product.service.domain;

import com.examples.springbootmonolithicstarter.domains.product.exception.ProductOutOfStockException;
import com.examples.springbootmonolithicstarter.domains.product.model.Product;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    public void reserveStock(Product product, int quantity) {
        if (!product.isAvailable()) {
            throw new ProductOutOfStockException(product.getId(), quantity, 0);
        }
        if (product.getStockQuantity() < quantity) {
            throw new ProductOutOfStockException(
                    product.getId(),
                    quantity,
                    product.getStockQuantity()
            );
        }
        product.removeStock(quantity);
    }

    public void releaseStock(Product product, int quantity) {
        product.addStock(quantity);
    }

    public boolean hasEnoughStock(Product product, int quantity) {
        return product.isAvailable() && product.getStockQuantity() >= quantity;
    }
}
