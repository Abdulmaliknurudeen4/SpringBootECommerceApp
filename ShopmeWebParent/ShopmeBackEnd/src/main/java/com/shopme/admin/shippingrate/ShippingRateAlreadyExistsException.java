package com.shopme.admin.shippingrate;

import java.io.Serializable;

public class ShippingRateAlreadyExistsException extends Exception implements Serializable {
    public ShippingRateAlreadyExistsException(String message) {
        super(message);
    }
}
