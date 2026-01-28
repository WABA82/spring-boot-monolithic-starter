package com.examples.springbootmonolithicstarter.domains.product.exception;

import com.examples.springbootmonolithicstarter.global.exception.BusinessException;
import com.examples.springbootmonolithicstarter.global.exception.ErrorCode;

public class ProductOutOfStockException extends BusinessException {

    public ProductOutOfStockException() {
        super(ErrorCode.PRODUCT_OUT_OF_STOCK);
    }

    public ProductOutOfStockException(Long productId, int requestedQuantity, int availableQuantity) {
        super(ErrorCode.PRODUCT_OUT_OF_STOCK,
                String.format("상품 재고가 부족합니다. ID: %d, 요청: %d, 가용: %d", productId, requestedQuantity, availableQuantity));
    }
}
