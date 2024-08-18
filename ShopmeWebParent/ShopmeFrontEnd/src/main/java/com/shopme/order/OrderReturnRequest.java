package com.shopme.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderReturnRequest {
    private Integer orderId;
    private String reason;
    private String note;
}
