package com.shopme.admin.order;

import com.shopme.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

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

    @Query("SELECT NEW com.shopme.entity.order.Order(o.id, o.orderTime, o.productCost, o.subtotal, o.total) FROM Order o " +
           "WHERE o.orderTime BETWEEN ?1 AND ?2 ORDER BY o.orderTime ASC")
    public List<Order> findByOrderTimeBetween(Date startTime, Date endTime);
}
