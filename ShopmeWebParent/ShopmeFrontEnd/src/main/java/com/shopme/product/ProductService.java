package com.shopme.product;

import com.shopme.entity.product.Product;
import com.shopme.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ProductService {
    public static final int PRODUCT_PER_PAGE = 10;
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> listByCategory(int pageNum, Integer categoryID) {
        String categoryIdMatch = "-" + categoryID + "-";
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

    public Product getProductById(Integer Id) throws ProductNotFoundException {
        try {
            return productRepository.findById(Id);
        } catch (NoSuchElementException e) {
            throw new ProductNotFoundException("Could not find any Product with id " + Id);
        }
    }

    public Page<Product> search(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE);
        return productRepository.search(keyword, pageable);
    }
}
