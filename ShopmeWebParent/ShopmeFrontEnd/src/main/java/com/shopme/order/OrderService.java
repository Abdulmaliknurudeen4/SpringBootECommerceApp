package com.shopme.order;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.entity.Address;
import com.shopme.entity.CartItem;
import com.shopme.entity.Customer;
import com.shopme.entity.order.*;
import com.shopme.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    public static final int ORDERS_PER_PAGE = 5;

    @Autowired
    private OrderRepository repo;

    private static OrderDetail getOrderDetail(CartItem cartItem, Order newOrder) {
        Product product = cartItem.getProduct();

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(newOrder);
        orderDetail.setProduct(product);
        orderDetail.setQuality(cartItem.getQuantity());
        orderDetail.setUnitPrice(product.getDiscountPrice());
        orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
        orderDetail.setSubtotal(cartItem.getSubtotal());
        orderDetail.setShippingCost(cartItem.getShippingCost());
        return orderDetail;
    }

    public Order getOrder(Integer id, Customer customer) {
        return repo.findByIdAndCustomer(id, customer);
    }

    public Order createOrder(Customer customer, Address address,
                             List<CartItem> cartItems,
                             PaymentMethod paymentMethod,
                             CheckoutInfo checkoutInfo) {

        Order newOrder = new Order();
        newOrder.setOrderTime(new Date());

        if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
            newOrder.setStatus(OrderStatus.PAID);
        } else {
            newOrder.setStatus(OrderStatus.NEW);
        }

        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0.0f);
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliveryDays(checkoutInfo.getDeliverDays());
        newOrder.setDeliverDate(checkoutInfo.getDeliveryDate());

        if (address == null) {
            newOrder.copyAddressFromCustomer();
        } else {
            newOrder.copyShippingAddress(address);
        }
        Set<OrderDetail> orderDetails = newOrder.getOrderDetail();
        /*Set<OrderDetail> orderDetails = cartItems.stream()
                .map(OrderService::getOrderDetail)
                .collect(Collectors.toSet());*/

        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = getOrderDetail(cartItem, newOrder);

            orderDetails.add(orderDetail);
        }
        return repo.save(newOrder);

    }

    public Page<Order> listForCustomerByPage(Customer customer, int pageNum,
                                             String sortFied, String sortDir, String keyword) {
        Sort sort = Sort.by(sortFied);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        if (keyword != null) {
            return repo.findAll(keyword, customer.getId(), pageable);
        }
        return repo.findAll(customer.getId(), pageable);
    }

    public void setOrderReturnRequested(OrderReturnRequest request, Customer customer) throws OrderNotFoundException {
        Order order = repo.findByIdAndCustomer(request.getOrderId(), customer);
        if (order == null) {
            throw new OrderNotFoundException("Order ID " + request.getOrderId() + " not Found");
        }
        if (order.isReturned()) return;

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setUpdatedTime(new Date());
        track.setStatus(OrderStatus.RETURN_REQUESTED);

        String notes = "Reason: " + request.getReason().trim();
        if (!"".equals(request.getNote())) {
            notes += ". " + request.getNote();
        }
        track.setNotes(notes);

        order.getOrderTracks().add(track);
        order.setStatus(OrderStatus.RETURN_REQUESTED);
        repo.save(order);
    }

}
