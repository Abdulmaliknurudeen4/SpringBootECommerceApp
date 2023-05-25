package com.shopme.cart;

import com.shopme.entity.CartItem;
import com.shopme.entity.Customer;
import com.shopme.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository repo;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveItem(){
        Integer customerId = 1;
        Integer productId = 1;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem newItem = new CartItem();
        newItem.setCustomer(customer);
        newItem.setProduct(product);
        newItem.setQuantity(1);

        CartItem save = repo.save(newItem);
        assertThat(save.getId()).isGreaterThan(0);
    }

    @Test
    public void findByCustomer(){
        Integer customerId = 1;
        Integer productId = 1;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        Iterable<CartItem> listItems = repo.findByCustomer(customer);
        listItems.forEach(System.out::println);
        assertThat(listItems).size().isGreaterThan(0);
        assertThat(listItems).extracting(CartItem::getCustomer).allMatch(c -> c.equals(customer));
        assertThat(listItems).extracting(CartItem::getProduct).contains((product));
    }

    @Test
    public void testFindByCustomerAndProduct(){
        Integer customerId = 1;
        Integer productId = 1;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem item = repo.findByCustomerAndProduct(customer, product);
        assertThat(item).isNotNull();
    }


    @Test
    public void testUpdateQuantity(){
        Integer customerId = 1;
        Integer productId = 1;
        Integer quantity = 5;

        repo.updateQuantity(quantity, productId, customerId);
        CartItem item = repo.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
        assertThat(item.getQuantity()).isEqualTo(quantity);
    }

    @Test
    public void testDeleteByCustomerAndProduct(){
        Integer customerId = 1;
        Integer productId = 1;
        repo.deleteByCustomerAndProduct(new Customer(customerId), new Product(productId));
        CartItem item = repo.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
        assertThat(item).isNull();
    }
}
