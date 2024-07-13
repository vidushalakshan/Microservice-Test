package com.order.order.common;

import lombok.Getter;

@Getter
public class ErrorOrderResponse implements OrderResponse{
    private final String errorMassage;

    public ErrorOrderResponse(String errorMassage) {
        this.errorMassage = errorMassage;
    }
}
