package com.shopme.admin.brand.controller;

import java.io.Serializable;

public class BrandNotFoundException extends Exception implements Serializable {
    public BrandNotFoundException(String message) {
        super(message);
    }
}
