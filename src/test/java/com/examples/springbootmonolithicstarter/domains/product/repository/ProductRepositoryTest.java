package com.examples.springbootmonolithicstarter.domains.product.repository;

import com.examples.springbootmonolithicstarter.domains.product.model.Product;
import com.examples.springbootmonolithicstarter.domains.product.model.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductRepository 통합 테스트")
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품을 저장하고 조회할 수 있다")
    void saveAndFind() {
        // given
        Product product = Product.create("테스트 상품", "설명", BigDecimal.valueOf(10000), 100);

        // when
        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        // then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("테스트 상품");
        assertThat(foundProduct.getPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(10000));
    }

    @Test
    @DisplayName("상태별로 상품을 조회할 수 있다")
    void findByStatus() {
        // given
        Product availableProduct = Product.create("판매중 상품", "설명", BigDecimal.valueOf(10000), 100);
        Product discontinuedProduct = Product.create("판매중지 상품", "설명", BigDecimal.valueOf(20000), 50);
        discontinuedProduct.discontinue();

        productRepository.save(availableProduct);
        productRepository.save(discontinuedProduct);

        // when
        List<Product> availableProducts = productRepository.findByStatus(ProductStatus.AVAILABLE);
        List<Product> discontinuedProducts = productRepository.findByStatus(ProductStatus.DISCONTINUED);

        // then
        assertThat(availableProducts).hasSize(1);
        assertThat(availableProducts.get(0).getName()).isEqualTo("판매중 상품");

        assertThat(discontinuedProducts).hasSize(1);
        assertThat(discontinuedProducts.get(0).getName()).isEqualTo("판매중지 상품");
    }

    @Test
    @DisplayName("이름으로 상품을 검색할 수 있다")
    void findByNameContaining() {
        // given
        productRepository.save(Product.create("노트북 프로", "설명", BigDecimal.valueOf(1000000), 10));
        productRepository.save(Product.create("노트북 에어", "설명", BigDecimal.valueOf(800000), 20));
        productRepository.save(Product.create("스마트폰", "설명", BigDecimal.valueOf(500000), 30));

        // when
        List<Product> results = productRepository.findByNameContaining("노트북");

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Product::getName)
                .containsExactlyInAnyOrder("노트북 프로", "노트북 에어");
    }

    @Test
    @DisplayName("상품 정보를 수정할 수 있다")
    void updateProduct() {
        // given
        Product product = Product.create("원래 이름", "원래 설명", BigDecimal.valueOf(10000), 100);
        Product savedProduct = productRepository.save(product);

        // when
        savedProduct.updateInfo("수정된 이름", "수정된 설명", BigDecimal.valueOf(20000));
        productRepository.flush();

        Product updatedProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        // then
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getName()).isEqualTo("수정된 이름");
        assertThat(updatedProduct.getPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(20000));
    }

    @Test
    @DisplayName("상품을 삭제할 수 있다")
    void deleteProduct() {
        // given
        Product product = Product.create("삭제할 상품", "설명", BigDecimal.valueOf(10000), 100);
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();

        // when
        productRepository.delete(savedProduct);

        // then
        assertThat(productRepository.findById(productId)).isEmpty();
    }
}
