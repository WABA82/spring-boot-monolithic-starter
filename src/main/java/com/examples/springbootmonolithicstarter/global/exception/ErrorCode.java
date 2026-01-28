package com.examples.springbootmonolithicstarter.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 오류가 발생했습니다."),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "상품을 찾을 수 없습니다."),
    PRODUCT_INVALID_PRICE(HttpStatus.BAD_REQUEST, "P002", "상품 가격이 유효하지 않습니다."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "P003", "상품 재고가 부족합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
