package com.examples.springbootmonolithicstarter.domains.product.exception;

import com.examples.springbootmonolithicstarter.global.exception.BusinessException;
import com.examples.springbootmonolithicstarter.global.exception.CommonErrorCode;
import com.examples.springbootmonolithicstarter.global.exception.ErrorCode;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException() {
        super(CommonErrorCode.PRODUCT_NOT_FOUND);
    }

    public ProductNotFoundException(Long productId) {
        super(CommonErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다. ID: " + productId);
    }
}
