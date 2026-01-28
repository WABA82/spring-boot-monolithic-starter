package com.examples.springbootmonolithicstarter.domains.product.controller;

import com.examples.springbootmonolithicstarter.domains.product.dto.request.CreateProductRequest;
import com.examples.springbootmonolithicstarter.domains.product.dto.request.UpdateProductRequest;
import com.examples.springbootmonolithicstarter.domains.product.dto.response.ProductResponse;
import com.examples.springbootmonolithicstarter.domains.product.service.application.ProductApplicationService;
import com.examples.springbootmonolithicstarter.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductApplicationService productApplicationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        ProductResponse response = productApplicationService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable Long productId
    ) {
        ProductResponse response = productApplicationService.getProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> response = productApplicationService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAvailableProducts() {
        List<ProductResponse> response = productApplicationService.getAvailableProducts();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam String name
    ) {
        List<ProductResponse> response = productApplicationService.searchProducts(name);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        ProductResponse response = productApplicationService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{productId}/stock/add")
    public ResponseEntity<ApiResponse<Void>> addStock(
            @PathVariable Long productId,
            @RequestParam int quantity
    ) {
        productApplicationService.addStock(productId, quantity);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{productId}/stock/remove")
    public ResponseEntity<ApiResponse<Void>> removeStock(
            @PathVariable Long productId,
            @RequestParam int quantity
    ) {
        productApplicationService.removeStock(productId, quantity);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{productId}/discontinue")
    public ResponseEntity<ApiResponse<Void>> discontinueProduct(
            @PathVariable Long productId
    ) {
        productApplicationService.discontinueProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{productId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateProduct(
            @PathVariable Long productId
    ) {
        productApplicationService.activateProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
