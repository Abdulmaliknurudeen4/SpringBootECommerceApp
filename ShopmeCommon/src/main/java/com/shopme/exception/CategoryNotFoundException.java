package com.shopme.exception;

import java.io.Serializable;

public class CategoryNotFoundException extends Exception implements Serializable {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
