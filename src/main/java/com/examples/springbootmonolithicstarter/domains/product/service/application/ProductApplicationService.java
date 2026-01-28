package com.examples.springbootmonolithicstarter.domains.product.service.application;

import com.examples.springbootmonolithicstarter.domains.product.dto.request.CreateProductRequest;
import com.examples.springbootmonolithicstarter.domains.product.dto.request.UpdateProductRequest;
import com.examples.springbootmonolithicstarter.domains.product.dto.response.ProductResponse;
import com.examples.springbootmonolithicstarter.domains.product.exception.ProductNotFoundException;
import com.examples.springbootmonolithicstarter.domains.product.model.Product;
import com.examples.springbootmonolithicstarter.domains.product.model.ProductStatus;
import com.examples.springbootmonolithicstarter.domains.product.repository.ProductRepository;
import com.examples.springbootmonolithicstarter.domains.product.service.domain.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductApplicationService {

    private final ProductRepository productRepository;
    private final StockService stockService;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.create(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );
        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }

    public ProductResponse getProduct(Long productId) {
        Product product = findProductById(productId);
        return ProductResponse.from(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    public List<ProductResponse> getAvailableProducts() {
        return productRepository.findByStatus(ProductStatus.AVAILABLE).stream()
                .map(ProductResponse::from)
                .toList();
    }

    public List<ProductResponse> searchProducts(String name) {
        return productRepository.findByNameContaining(name).stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = findProductById(productId);
        product.updateInfo(request.name(), request.description(), request.price());
        return ProductResponse.from(product);
    }

    @Transactional
    public void addStock(Long productId, int quantity) {
        Product product = findProductById(productId);
        stockService.releaseStock(product, quantity);
    }

    @Transactional
    public void removeStock(Long productId, int quantity) {
        Product product = findProductById(productId);
        stockService.reserveStock(product, quantity);
    }

    @Transactional
    public void discontinueProduct(Long productId) {
        Product product = findProductById(productId);
        product.discontinue();
    }

    @Transactional
    public void activateProduct(Long productId) {
        Product product = findProductById(productId);
        product.activate();
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
