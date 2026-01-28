package com.examples.springbootmonolithicstarter.domains.product.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Product 엔티티")
class ProductTest {

    @Nested
    @DisplayName("생성")
    class Creation {

        @Test
        @DisplayName("상품을 생성할 수 있다")
        void createProduct() {
            Product product = Product.create(
                    "테스트 상품",
                    "상품 설명",
                    BigDecimal.valueOf(10000),
                    100
            );

            assertThat(product.getName()).isEqualTo("테스트 상품");
            assertThat(product.getDescription()).isEqualTo("상품 설명");
            assertThat(product.getPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(10000));
            assertThat(product.getStockQuantity()).isEqualTo(100);
            assertThat(product.getStatus()).isEqualTo(ProductStatus.AVAILABLE);
            assertThat(product.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("재고 관리")
    class StockManagement {

        @Test
        @DisplayName("재고를 추가할 수 있다")
        void addStock() {
            Product product = createProduct(100);

            product.addStock(50);

            assertThat(product.getStockQuantity()).isEqualTo(150);
        }

        @Test
        @DisplayName("0 이하의 수량을 추가하면 예외가 발생한다")
        void addStockWithZeroOrNegativeThrowsException() {
            Product product = createProduct(100);

            assertThatThrownBy(() -> product.addStock(0))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> product.addStock(-10))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("재고를 차감할 수 있다")
        void removeStock() {
            Product product = createProduct(100);

            product.removeStock(30);

            assertThat(product.getStockQuantity()).isEqualTo(70);
        }

        @Test
        @DisplayName("재고보다 많은 수량을 차감하면 예외가 발생한다")
        void removeStockExceedingQuantityThrowsException() {
            Product product = createProduct(100);

            assertThatThrownBy(() -> product.removeStock(150))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("재고가 부족합니다");
        }

        @Test
        @DisplayName("0 이하의 수량을 차감하면 예외가 발생한다")
        void removeStockWithZeroOrNegativeThrowsException() {
            Product product = createProduct(100);

            assertThatThrownBy(() -> product.removeStock(0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("상태 변경")
    class StatusChange {

        @Test
        @DisplayName("상품을 판매 중지할 수 있다")
        void discontinue() {
            Product product = createProduct(100);

            product.discontinue();

            assertThat(product.getStatus()).isEqualTo(ProductStatus.DISCONTINUED);
            assertThat(product.isAvailable()).isFalse();
        }

        @Test
        @DisplayName("판매 중지된 상품을 다시 활성화할 수 있다")
        void activate() {
            Product product = createProduct(100);
            product.discontinue();

            product.activate();

            assertThat(product.getStatus()).isEqualTo(ProductStatus.AVAILABLE);
            assertThat(product.isAvailable()).isTrue();
        }

        @Test
        @DisplayName("재고가 0이면 판매 가능하지 않다")
        void notAvailableWhenOutOfStock() {
            Product product = createProduct(0);

            assertThat(product.isAvailable()).isFalse();
        }
    }

    @Nested
    @DisplayName("가격 계산")
    class PriceCalculation {

        @Test
        @DisplayName("총 가격을 계산할 수 있다")
        void calculateTotalPrice() {
            Product product = Product.create(
                    "테스트 상품",
                    "설명",
                    BigDecimal.valueOf(1000),
                    100
            );

            Money totalPrice = product.calculateTotalPrice(5);

            assertThat(totalPrice.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        }
    }

    private Product createProduct(int stockQuantity) {
        return Product.create("테스트 상품", "설명", BigDecimal.valueOf(10000), stockQuantity);
    }
}
