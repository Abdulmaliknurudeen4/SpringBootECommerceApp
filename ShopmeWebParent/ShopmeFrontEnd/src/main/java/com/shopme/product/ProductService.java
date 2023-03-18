package com.shopme.product;

import com.shopme.entity.Product;
import com.shopme.exception.ProductNotFoundException;
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

    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Could not find any Product with alias " + alias);
        }
        return product;
    }

    public Page<Product> search(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE);
        return productRepository.search(keyword, pageable);
    }
}
