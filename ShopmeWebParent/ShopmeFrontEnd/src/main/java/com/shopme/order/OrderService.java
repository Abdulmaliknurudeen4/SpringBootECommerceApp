package com.shopme.order;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.entity.Address;
import com.shopme.entity.CartItem;
import com.shopme.entity.Customer;
import com.shopme.entity.order.Order;
import com.shopme.entity.order.OrderDetail;
import com.shopme.entity.order.OrderStatus;
import com.shopme.entity.order.PaymentMethod;
import com.shopme.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    @Autowired
    private OrderRepository repo;

    public Order createOrder(Customer customer, Address address,
                             List<CartItem> cartItems,
                             PaymentMethod paymentMethod,
                             CheckoutInfo checkoutInfo){

        Order newOrder = new Order();
        newOrder.setOrderTime(new Date());
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0.0f);
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliveryDays(checkoutInfo.getDeliverDays());
        newOrder.setDeliverDate(checkoutInfo.getDeliveryDate());

        if(address == null){
            newOrder.copyAddressFromCustomer();
        }else{
            newOrder.copyShippingAddress(address);
        }
        Set<OrderDetail> orderDetails = newOrder.getOrderDetail();
        /*Set<OrderDetail> orderDetails = cartItems.stream()
                .map(OrderService::getOrderDetail)
                .collect(Collectors.toSet());*/

        for (CartItem cartItem: cartItems){
            OrderDetail orderDetail = getOrderDetail(cartItem, newOrder);

            orderDetails.add(orderDetail);
        }
        return repo.save(newOrder);

    }

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
}
