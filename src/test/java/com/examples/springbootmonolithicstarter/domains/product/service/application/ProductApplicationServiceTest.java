package com.examples.springbootmonolithicstarter.domains.product.service.application;

import com.examples.springbootmonolithicstarter.domains.product.dto.request.CreateProductRequest;
import com.examples.springbootmonolithicstarter.domains.product.dto.request.UpdateProductRequest;
import com.examples.springbootmonolithicstarter.domains.product.dto.response.ProductResponse;
import com.examples.springbootmonolithicstarter.domains.product.exception.ProductNotFoundException;
import com.examples.springbootmonolithicstarter.domains.product.model.Product;
import com.examples.springbootmonolithicstarter.domains.product.model.ProductStatus;
import com.examples.springbootmonolithicstarter.domains.product.repository.ProductRepository;
import com.examples.springbootmonolithicstarter.domains.product.service.domain.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("ProductApplicationService")
@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    @InjectMocks
    private ProductApplicationService productApplicationService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockService stockService;

    @Nested
    @DisplayName("상품 생성")
    class CreateProduct {

        @Test
        @DisplayName("상품을 생성할 수 있다")
        void createProduct() {
            // given
            CreateProductRequest request = new CreateProductRequest(
                    "새 상품",
                    "설명",
                    BigDecimal.valueOf(10000),
                    100
            );
            Product savedProduct = Product.create(
                    request.name(),
                    request.description(),
                    request.price(),
                    request.stockQuantity()
            );
            given(productRepository.save(any(Product.class))).willReturn(savedProduct);

            // when
            ProductResponse response = productApplicationService.createProduct(request);

            // then
            assertThat(response.name()).isEqualTo("새 상품");
            assertThat(response.price()).isEqualByComparingTo(BigDecimal.valueOf(10000));
            assertThat(response.stockQuantity()).isEqualTo(100);
            then(productRepository).should().save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("상품 조회")
    class GetProduct {

        @Test
        @DisplayName("ID로 상품을 조회할 수 있다")
        void getProductById() {
            // given
            Long productId = 1L;
            Product product = createProduct();
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            ProductResponse response = productApplicationService.getProduct(productId);

            // then
            assertThat(response.name()).isEqualTo("테스트 상품");
        }

        @Test
        @DisplayName("존재하지 않는 상품을 조회하면 예외가 발생한다")
        void getProductNotFound() {
            // given
            Long productId = 999L;
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productApplicationService.getProduct(productId))
                    .isInstanceOf(ProductNotFoundException.class);
        }

        @Test
        @DisplayName("모든 상품을 조회할 수 있다")
        void getAllProducts() {
            // given
            List<Product> products = List.of(createProduct(), createProduct());
            given(productRepository.findAll()).willReturn(products);

            // when
            List<ProductResponse> responses = productApplicationService.getAllProducts();

            // then
            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("판매 가능한 상품만 조회할 수 있다")
        void getAvailableProducts() {
            // given
            List<Product> products = List.of(createProduct());
            given(productRepository.findByStatus(ProductStatus.AVAILABLE)).willReturn(products);

            // when
            List<ProductResponse> responses = productApplicationService.getAvailableProducts();

            // then
            assertThat(responses).hasSize(1);
            then(productRepository).should().findByStatus(ProductStatus.AVAILABLE);
        }
    }

    @Nested
    @DisplayName("상품 수정")
    class UpdateProduct {

        @Test
        @DisplayName("상품 정보를 수정할 수 있다")
        void updateProduct() {
            // given
            Long productId = 1L;
            Product product = createProduct();
            UpdateProductRequest request = new UpdateProductRequest(
                    "수정된 상품",
                    "수정된 설명",
                    BigDecimal.valueOf(20000)
            );
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            ProductResponse response = productApplicationService.updateProduct(productId, request);

            // then
            assertThat(response.name()).isEqualTo("수정된 상품");
            assertThat(response.price()).isEqualByComparingTo(BigDecimal.valueOf(20000));
        }
    }

    @Nested
    @DisplayName("재고 관리")
    class StockManagement {

        @Test
        @DisplayName("재고를 추가할 수 있다")
        void addStock() {
            // given
            Long productId = 1L;
            Product product = createProduct();
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            productApplicationService.addStock(productId, 50);

            // then
            then(stockService).should().releaseStock(product, 50);
        }

        @Test
        @DisplayName("재고를 차감할 수 있다")
        void removeStock() {
            // given
            Long productId = 1L;
            Product product = createProduct();
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            productApplicationService.removeStock(productId, 30);

            // then
            then(stockService).should().reserveStock(product, 30);
        }

        @Test
        @DisplayName("존재하지 않는 상품의 재고를 추가하면 예외가 발생한다")
        void addStockToNonExistentProduct() {
            // given
            Long productId = 999L;
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productApplicationService.addStock(productId, 50))
                    .isInstanceOf(ProductNotFoundException.class);
            then(stockService).should(never()).releaseStock(any(), anyInt());
        }
    }

    @Nested
    @DisplayName("상품 상태 변경")
    class StatusChange {

        @Test
        @DisplayName("상품을 판매 중지할 수 있다")
        void discontinueProduct() {
            // given
            Long productId = 1L;
            Product product = createProduct();
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            productApplicationService.discontinueProduct(productId);

            // then
            assertThat(product.getStatus()).isEqualTo(ProductStatus.DISCONTINUED);
        }

        @Test
        @DisplayName("상품을 활성화할 수 있다")
        void activateProduct() {
            // given
            Long productId = 1L;
            Product product = createProduct();
            product.discontinue();
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            productApplicationService.activateProduct(productId);

            // then
            assertThat(product.getStatus()).isEqualTo(ProductStatus.AVAILABLE);
        }
    }

    private Product createProduct() {
        return Product.create("테스트 상품", "설명", BigDecimal.valueOf(10000), 100);
    }
}
