package com.shopme.admin.brand.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Brand not Found")
public class BrandNotFoundRestException extends Exception implements Serializable {
}
