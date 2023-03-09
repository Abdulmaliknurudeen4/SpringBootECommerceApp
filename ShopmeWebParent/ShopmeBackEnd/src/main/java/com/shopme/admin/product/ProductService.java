package com.shopme.admin.product;

import com.shopme.admin.product.controller.ProductNotFoundException;
import com.shopme.admin.user.UserNotFoundExcpetion;
import com.shopme.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll();
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }
        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            product.setAlias(product.getName().replaceAll(" ", "-"));
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
        }

        product.setUpdatedTime(new Date());
        return productRepository.save(product);
    }

    public boolean isProductUnique(String name, Integer id){
        boolean isCreatingNew = (id == null || id == 0);
        Product productByName = productRepository.findByName(name);

        if (productByName == null)
            return true;
        if (isCreatingNew) {
            return productByName == null;
        } else {
            return productByName.getId() == id;
        }
    }

    public void setEnableProduct(boolean status, Integer id) throws ProductNotFoundException {
        Long userCount = productRepository.countById(id);
        if (userCount == null || userCount == 0) {
            throw new ProductNotFoundException("Product not Present !!");
        }
        productRepository.EnableProductStatus(id, status);
    }

    public void deleteProduct(Integer id) throws ProductNotFoundException{
        Long userCount = productRepository.countById(id);
        if (userCount == null || userCount == 0) {
            throw new ProductNotFoundException("Product not Present !!");
        }
        productRepository.deleteById(id);
    }

    public Product getProduct(Integer id ) throws ProductNotFoundException {
       try{
           return productRepository.findById(id).get();
       }catch (NoSuchElementException e){
           throw new ProductNotFoundException("Counldn't find any product with the ID " + id);
       }
    }
}
