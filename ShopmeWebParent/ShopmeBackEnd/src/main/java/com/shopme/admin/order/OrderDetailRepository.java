package com.shopme.admin.order;

import com.shopme.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OrderDetailRepository extends CrudRepository<OrderDetail, Integer> {

    @Query(" SELECT NEW com.shopme.entity.order.OrderDetail(d.product.category.name, d.quality, d.productCost, " +
           "d.shippingCost, d.subtotal) FROM OrderDetail d WHERE " +
           "d.order.orderTime BETWEEN ?1 and ?2")
    public List<OrderDetail> findWithCategoryAndTimeBetween(Date startTime, Date endTime);

    @Query(" SELECT NEW com.shopme.entity.order.OrderDetail(d.quality,d .product.name, d.productCost, " +
           "d.shippingCost, d.subtotal) FROM OrderDetail d WHERE " +
           "d.order.orderTime BETWEEN ?1 and ?2")
    public List<OrderDetail> findWithProductAndTimeBetween(Date startTime, Date endTime);
}
