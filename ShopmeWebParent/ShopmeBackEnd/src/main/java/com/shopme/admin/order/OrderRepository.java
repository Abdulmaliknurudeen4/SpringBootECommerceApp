package com.shopme.admin.order;

import com.shopme.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

@SuppressWarnings("ALL")
public interface OrderRepository extends PagingAndSortingRepository<Order, Integer>, CrudRepository<Order, Integer> {

    @Query("SELECT o FROM Order o WHERE CONCAT('#', o.id) LIKE %?1% OR " +
           "CONCAT(o.firstName, ' ', o.lastName) LIKE %?1% OR "+
           "o.firstName LIKE %?1% OR " +
           "o.lastName LIKE %?1% OR o.phoneNumber LIKE %?1% OR " +
           "o.addressLine1 LIKE %?1% OR o.addressLine2 LIKE %?1% OR " +
           "o.postalCode LIKE %?1% OR o.city LIKE %?1% OR " +
           "o.state LIKE %?1% OR o.country LIKE %?1% OR " +
           "o.paymentMethod LIKE %?1% OR o.status LIKE %?1% OR " +
           "o.customer.firstName LIKE %?1% OR " +
           "o.customer.lastName LIKE %?1%")
    public Page<Order> findAll(String keyword, Pageable pageable);

    public Long countById(Integer id);
}
