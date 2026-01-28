package com.examples.springbootmonolithicstarter.domains.product.service.domain;

import com.examples.springbootmonolithicstarter.domains.product.exception.ProductOutOfStockException;
import com.examples.springbootmonolithicstarter.domains.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StockService 도메인 서비스")
class StockServiceTest {

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService();
    }

    @Nested
    @DisplayName("재고 예약")
    class ReserveStock {

        @Test
        @DisplayName("충분한 재고가 있으면 예약할 수 있다")
        void reserveStockSuccessfully() {
            Product product = createProduct(100);

            stockService.reserveStock(product, 30);

            assertThat(product.getStockQuantity()).isEqualTo(70);
        }

        @Test
        @DisplayName("재고가 부족하면 예외가 발생한다")
        void reserveStockWithInsufficientStock() {
            Product product = createProduct(10);

            assertThatThrownBy(() -> stockService.reserveStock(product, 20))
                    .isInstanceOf(ProductOutOfStockException.class);
        }

        @Test
        @DisplayName("판매 중지된 상품은 예약할 수 없다")
        void reserveStockForDiscontinuedProduct() {
            Product product = createProduct(100);
            product.discontinue();

            assertThatThrownBy(() -> stockService.reserveStock(product, 10))
                    .isInstanceOf(ProductOutOfStockException.class);
        }
    }

    @Nested
    @DisplayName("재고 해제")
    class ReleaseStock {

        @Test
        @DisplayName("재고를 해제할 수 있다")
        void releaseStockSuccessfully() {
            Product product = createProduct(100);

            stockService.releaseStock(product, 50);

            assertThat(product.getStockQuantity()).isEqualTo(150);
        }
    }

    @Nested
    @DisplayName("재고 확인")
    class CheckStock {

        @Test
        @DisplayName("충분한 재고가 있으면 true를 반환한다")
        void hasEnoughStock() {
            Product product = createProduct(100);

            boolean result = stockService.hasEnoughStock(product, 50);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("재고가 부족하면 false를 반환한다")
        void hasNotEnoughStock() {
            Product product = createProduct(10);

            boolean result = stockService.hasEnoughStock(product, 50);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("판매 중지된 상품은 false를 반환한다")
        void discontinuedProductHasNoStock() {
            Product product = createProduct(100);
            product.discontinue();

            boolean result = stockService.hasEnoughStock(product, 10);

            assertThat(result).isFalse();
        }
    }

    private Product createProduct(int stockQuantity) {
        return Product.create("테스트 상품", "설명", BigDecimal.valueOf(10000), stockQuantity);
    }
}
