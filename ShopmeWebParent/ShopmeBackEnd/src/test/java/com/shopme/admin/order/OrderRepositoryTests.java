package com.shopme.admin.order;

import com.shopme.entity.Customer;
import com.shopme.entity.order.*;
import com.shopme.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepositoryTests {

    @Autowired
    private OrderRepository repo;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewOrderWithSingleProduct() {
        Customer customer = entityManager.find(Customer.class, 1);
        Product product = entityManager.find(Product.class, 1);

        Order mainOrder = new Order();
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        mainOrder.setShippingCost(10);
        mainOrder.setProductCost(product.getCost());
        mainOrder.setTax(0);
        mainOrder.setSubtotal(product.getPrice());
        mainOrder.setTotal(product.getPrice() + 10);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setStatus(OrderStatus.NEW);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliveryDays(1);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setOrder(mainOrder);
        orderDetail.setProductCost(product.getCost());
        orderDetail.setShippingCost(10);
        orderDetail.setQuality(1);
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setUnitPrice(product.getPrice());

        mainOrder.getOrderDetail().add(orderDetail);

        Order savedOrder = repo.save(mainOrder);

        assertThat(savedOrder.getId()).isGreaterThan(0);


    }

    @Test
    public void testCreateNewOrderWithMultipleProducts() {
        Customer customer = entityManager.find(Customer.class, 2);
        Product product1 = entityManager.find(Product.class, 3);
        Product product2 = entityManager.find(Product.class, 5);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product1);
        orderDetail.setOrder(mainOrder);
        orderDetail.setProductCost(product1.getCost());
        orderDetail.setShippingCost(10);
        orderDetail.setQuality(1);
        orderDetail.setSubtotal(product1.getPrice());
        orderDetail.setUnitPrice(product1.getPrice());

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProduct(product2);
        orderDetail2.setOrder(mainOrder);
        orderDetail2.setProductCost(product2.getCost());
        orderDetail2.setShippingCost(20);
        orderDetail2.setQuality(2);
        orderDetail2.setSubtotal(product2.getPrice() * 2);
        orderDetail2.setUnitPrice(product2.getPrice());

        mainOrder.getOrderDetail().add(orderDetail);
        mainOrder.getOrderDetail().add(orderDetail2);

        mainOrder.setShippingCost(30);
        mainOrder.setProductCost(product1.getCost() + product2.getCost());
        mainOrder.setTax(0);
        float subTotal = product1.getPrice() + (product2.getPrice() * 2);
        mainOrder.setSubtotal(subTotal);
        mainOrder.setTotal(subTotal + 30);

        mainOrder.setPaymentMethod(PaymentMethod.COD);
        mainOrder.setStatus(OrderStatus.PROCESSING);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliveryDays(3);

        Order savedOrder = repo.save(mainOrder);
        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void testListOrders() {
        Iterable<Order> orders = repo.findAll();

        assertThat(orders).hasSizeGreaterThan(0);

        orders.forEach(System.out::println);
    }

    @Test
    public void testUpdateOrder() {
        Integer orderId = 2;
        Order order = repo.findById(orderId).get();

        order.setStatus(OrderStatus.SHIPPING);
        order.setPaymentMethod(PaymentMethod.COD);
        order.setOrderTime(new Date());
        order.setDeliveryDays(2);

        Order updateOrder = repo.save(order);

        assertThat(updateOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }

    @Test
    public void testGetOrder() {
        Integer orderId = 3;
        Order order = repo.findById(orderId).get();
        assertThat(order).isNotNull();
        System.out.println(order);
    }

    @Test
    public void testDeleteOrder() {
        Integer orderId = 3;
        repo.deleteById(orderId);
        Optional<Order> order = repo.findById(orderId);
        assertThat(order).isNotPresent();
    }

    @Test
    public void testUpdateOrderTrack() {
        Integer orderId = 6;
        Order order = repo.findById(orderId).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date().toString());
        newTrack.setStatus(OrderStatus.NEW);
        newTrack.setNotes(OrderStatus.NEW.defaultDescription());

        OrderTrack processingTrack = new OrderTrack();
        processingTrack.setOrder(order);
        processingTrack.setUpdatedTime(new Date().toString());
        processingTrack.setStatus(OrderStatus.PROCESSING);
        processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());


        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        orderTracks.add(processingTrack);

        Order updatedOrder = repo.save(order);

        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
    }

    @Test
    public void testFindbyOrderTimeBetwee() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-08-01");
        Date endTime = dateFormatter.parse("2021-08-31");

        List<Order> listOrders = repo.findByOrderTimeBetween(startTime, endTime);

        assertThat(listOrders.size()).isGreaterThan(0);

        for(Order order: listOrders){
            System.out.printf("%s | %s | %.2f | %.2f | %.2f \n",
                    order.getId(), order.getOrderTime(), order.getProductCost(),
                    order.getSubtotal(), order.getTotal());
        }

    }
}
