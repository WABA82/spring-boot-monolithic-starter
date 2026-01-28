package com.examples.springbootmonolithicstarter.domains.product.repository;

import com.examples.springbootmonolithicstarter.domains.product.model.Product;
import com.examples.springbootmonolithicstarter.domains.product.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByNameContaining(String name);
}
