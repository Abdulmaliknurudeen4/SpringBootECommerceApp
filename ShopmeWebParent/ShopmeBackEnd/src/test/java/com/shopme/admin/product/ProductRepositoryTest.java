package com.shopme.admin.product;

import com.shopme.entity.Brand;
import com.shopme.entity.Category;
import com.shopme.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct(){
        // Get Category and Brand
        Brand brand = entityManager.find(Brand.class, 38);
        Category category = entityManager.find(Category.class, 6);

        Product product = new Product();
        product.setAlias("Dell Inspiron");
        product.setName("Dell Inspiron d3");
        product.setShortDescription("A good laptop from Dell");
        product.setFullDescription("Lorem Ipsum");

        product.setBrand(brand);
        product.setCategory(category);
        product.setEnabled(true);

        product.setPrice(456);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

       Product product1 = productRepository.save(product);
       assertThat(product1).isNotNull();
       assertThat(product1.getId()).isGreaterThan(0);

    }

    @Test
    public void listAllProduct(){
        Iterable<Product> products = productRepository.findAll();
        products.forEach(System.out::println);
        assertThat(0).isEqualTo(0);
    }

    @Test
    public void testGetProduct(){
        Integer id = 2;
        Product p = productRepository.findById(id).get();
        assertThat(p).isNotNull();
    }

    @Test
    public void updateProduct(){
        Integer id = 1;
        Product p = productRepository.findById(id).get();
        p.setPrice(499);

        productRepository.save(p);
        Product updatedProduct = entityManager.find(Product.class, id);
        assertThat(p.getPrice()).isEqualTo(updatedProduct.getPrice());
    }

    @Test
    public void deleteProduct(){
        Integer id = 2;
        productRepository.deleteById(id);

        Optional<Product> present = productRepository.findById(id);
        assertThat(!present.isPresent());
    }

}
