package com.shopme.product;

import com.shopme.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    public static final int PRODUCT_PER_PAGE = 10;
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> listByCategory(int pageNum, Integer categoryID) {
        String categoryIdMatch = "-" + String.valueOf(categoryID) + "-";
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE);

        return productRepository.listByCategory(categoryID, categoryIdMatch, pageable);
    }
}
