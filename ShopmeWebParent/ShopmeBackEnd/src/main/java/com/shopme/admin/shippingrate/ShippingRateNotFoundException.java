package com.shopme.admin.shippingrate;

import java.io.Serializable;

public class ShippingRateNotFoundException extends Exception implements Serializable {
    public ShippingRateNotFoundException(String message) {
        super(message);
    }
}
