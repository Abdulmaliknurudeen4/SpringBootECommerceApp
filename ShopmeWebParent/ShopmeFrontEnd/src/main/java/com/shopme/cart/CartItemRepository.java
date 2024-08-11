package com.shopme.cart;

import com.shopme.entity.CartItem;
import com.shopme.entity.Customer;
import com.shopme.entity.product.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CartItemRepository extends CrudRepository<CartItem, Integer> {
    List<CartItem> findByCustomer(Customer customer);

    @Query("SELECT c FROM CartItem c WHERE c.customer=?1 AND c.product=?2")
    CartItem findByCustomerAndProduct(Customer customer, Product product);

    @Modifying
    void deleteByCustomerAndProduct(Customer customer, Product product);

    @Modifying
    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.product.id = ?2 AND c.customer.id = ?3")
    void updateQuantity(Integer quantity, Integer productid, Integer customerid);

    @Modifying
    @Query("DELETE CartItem c WHERE c.product.id = ?1 AND c.customer.id = ?2")
    void deleteByCustomerAndProduct(Integer productid, Integer customerid);

    @Modifying
    @Query("DELETE CartItem c WHERE c.customer.id = ?1")
    public void deleteByCustomer(Integer customerId);
}
