package com.shopme.admin.product;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.entity.product.Product;
import com.shopme.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductService {
    public static final Integer PRODUCTS_PER_PAGE = 5;
    @Autowired
    private ProductRepository productRepository;

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll(Sort.by("name").ascending());
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
        Product updatedProduct = productRepository.save(product);

        productRepository.updateReviewCountAndAverageRating(updatedProduct.getId());
        return updatedProduct;
    }

    public void saveProductPrice(Product productInForm) {
        Product productInDB = productRepository.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());
        productRepository.save(productInDB);
    }

    public boolean isProductUnique(String name, Integer id) {
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

    public void deleteProduct(Integer id) throws ProductNotFoundException {
        Long userCount = productRepository.countById(id);
        if (userCount == null || userCount == 0) {
            throw new ProductNotFoundException("Product not Present !!");
        }
        productRepository.deleteById(id);
    }

    public Product getProduct(Integer id) throws ProductNotFoundException {
        try {
            return productRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ProductNotFoundException("Counldn't find any product with the ID " + id);
        }
    }


    public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page = null;
        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page = productRepository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            }
            page = productRepository.findAll(keyword, pageable);
        }else{

            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page = productRepository.findAllInCategory(categoryId, categoryIdMatch, pageable);
            }else{
                page = productRepository.findAll(pageable);
            }

        }



        helper.udpateModelAttributes(pageNum, page);
    }

    public void searchProduct(int pageNum, PagingAndSortingHelper helper){
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page = productRepository.searchProductByName(keyword, pageable);
//        page.get().forEach(System.out::println);

        helper.udpateModelAttributes(pageNum, page);
    }
}
