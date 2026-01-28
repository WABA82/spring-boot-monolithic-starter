package com.examples.springbootmonolithicstarter.domains.product.controller;

import com.examples.springbootmonolithicstarter.domains.product.dto.request.CreateProductRequest;
import com.examples.springbootmonolithicstarter.domains.product.dto.request.UpdateProductRequest;
import com.examples.springbootmonolithicstarter.domains.product.dto.response.ProductResponse;
import com.examples.springbootmonolithicstarter.domains.product.exception.ProductNotFoundException;
import com.examples.springbootmonolithicstarter.domains.product.model.ProductStatus;
import com.examples.springbootmonolithicstarter.domains.product.service.application.ProductApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ProductController 통합 테스트")
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductApplicationService productApplicationService;

    @Nested
    @DisplayName("POST /api/products")
    class CreateProduct {

        @Test
        @DisplayName("상품을 생성할 수 있다")
        void createProduct() throws Exception {
            // given
            CreateProductRequest request = new CreateProductRequest(
                    "새 상품",
                    "상품 설명",
                    BigDecimal.valueOf(10000),
                    100
            );
            ProductResponse response = createProductResponse(1L, "새 상품", BigDecimal.valueOf(10000), 100);
            given(productApplicationService.createProduct(any())).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("새 상품"))
                    .andExpect(jsonPath("$.data.price").value(10000))
                    .andExpect(jsonPath("$.data.stockQuantity").value(100));
        }

        @Test
        @DisplayName("상품명이 없으면 400 에러가 발생한다")
        void createProductWithoutName() throws Exception {
            // given
            CreateProductRequest request = new CreateProductRequest(
                    "",
                    "상품 설명",
                    BigDecimal.valueOf(10000),
                    100
            );

            // when & then
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("가격이 음수면 400 에러가 발생한다")
        void createProductWithNegativePrice() throws Exception {
            // given
            CreateProductRequest request = new CreateProductRequest(
                    "상품",
                    "설명",
                    BigDecimal.valueOf(-1000),
                    100
            );

            // when & then
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/products/{productId}")
    class GetProduct {

        @Test
        @DisplayName("상품을 조회할 수 있다")
        void getProduct() throws Exception {
            // given
            Long productId = 1L;
            ProductResponse response = createProductResponse(productId, "테스트 상품", BigDecimal.valueOf(10000), 100);
            given(productApplicationService.getProduct(productId)).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/products/{productId}", productId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(productId))
                    .andExpect(jsonPath("$.data.name").value("테스트 상품"));
        }

        @Test
        @DisplayName("존재하지 않는 상품을 조회하면 404 에러가 발생한다")
        void getProductNotFound() throws Exception {
            // given
            Long productId = 999L;
            given(productApplicationService.getProduct(productId))
                    .willThrow(new ProductNotFoundException(productId));

            // when & then
            mockMvc.perform(get("/api/products/{productId}", productId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("P001"));
        }
    }

    @Nested
    @DisplayName("GET /api/products")
    class GetAllProducts {

        @Test
        @DisplayName("모든 상품을 조회할 수 있다")
        void getAllProducts() throws Exception {
            // given
            List<ProductResponse> responses = List.of(
                    createProductResponse(1L, "상품1", BigDecimal.valueOf(10000), 100),
                    createProductResponse(2L, "상품2", BigDecimal.valueOf(20000), 200)
            );
            given(productApplicationService.getAllProducts()).willReturn(responses);

            // when & then
            mockMvc.perform(get("/api/products"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/products/search")
    class SearchProducts {

        @Test
        @DisplayName("이름으로 상품을 검색할 수 있다")
        void searchProducts() throws Exception {
            // given
            String searchName = "노트북";
            List<ProductResponse> responses = List.of(
                    createProductResponse(1L, "노트북 프로", BigDecimal.valueOf(1000000), 10)
            );
            given(productApplicationService.searchProducts(searchName)).willReturn(responses);

            // when & then
            mockMvc.perform(get("/api/products/search")
                            .param("name", searchName))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].name").value("노트북 프로"));
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{productId}")
    class UpdateProduct {

        @Test
        @DisplayName("상품 정보를 수정할 수 있다")
        void updateProduct() throws Exception {
            // given
            Long productId = 1L;
            UpdateProductRequest request = new UpdateProductRequest(
                    "수정된 상품",
                    "수정된 설명",
                    BigDecimal.valueOf(20000)
            );
            ProductResponse response = createProductResponse(productId, "수정된 상품", BigDecimal.valueOf(20000), 100);
            given(productApplicationService.updateProduct(eq(productId), any())).willReturn(response);

            // when & then
            mockMvc.perform(put("/api/products/{productId}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.name").value("수정된 상품"))
                    .andExpect(jsonPath("$.data.price").value(20000));
        }
    }

    @Nested
    @DisplayName("POST /api/products/{productId}/stock")
    class StockManagement {

        @Test
        @DisplayName("재고를 추가할 수 있다")
        void addStock() throws Exception {
            // given
            Long productId = 1L;
            willDoNothing().given(productApplicationService).addStock(productId, 50);

            // when & then
            mockMvc.perform(post("/api/products/{productId}/stock/add", productId)
                            .param("quantity", "50"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("재고를 차감할 수 있다")
        void removeStock() throws Exception {
            // given
            Long productId = 1L;
            willDoNothing().given(productApplicationService).removeStock(productId, 30);

            // when & then
            mockMvc.perform(post("/api/products/{productId}/stock/remove", productId)
                            .param("quantity", "30"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("POST /api/products/{productId}/discontinue")
    class DiscontinueProduct {

        @Test
        @DisplayName("상품을 판매 중지할 수 있다")
        void discontinueProduct() throws Exception {
            // given
            Long productId = 1L;
            willDoNothing().given(productApplicationService).discontinueProduct(productId);

            // when & then
            mockMvc.perform(post("/api/products/{productId}/discontinue", productId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("존재하지 않는 상품을 판매 중지하면 404 에러가 발생한다")
        void discontinueProductNotFound() throws Exception {
            // given
            Long productId = 999L;
            willThrow(new ProductNotFoundException(productId))
                    .given(productApplicationService).discontinueProduct(productId);

            // when & then
            mockMvc.perform(post("/api/products/{productId}/discontinue", productId))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    private ProductResponse createProductResponse(Long id, String name, BigDecimal price, Integer stockQuantity) {
        return new ProductResponse(
                id,
                name,
                "설명",
                price,
                stockQuantity,
                ProductStatus.AVAILABLE,
                true,
                LocalDateTime.now(),
                null
        );
    }
}
