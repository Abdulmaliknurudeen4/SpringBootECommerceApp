package com.shopme.checkout;

import com.shopme.entity.CartItem;
import com.shopme.entity.ShippingRate;
import com.shopme.entity.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {

    private static final int DIM_DIVISOR = 139;

    public CheckoutInfo prepareCheckOut(List<CartItem> cartItems, ShippingRate shippingRate) {
        CheckoutInfo checkoutInfo = new CheckoutInfo();
        float productCost = calculateProductCost(cartItems);
        float productTotal = calcualteProductTotal(cartItems);
        float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
        float paymentTotal = productTotal + shippingCostTotal;


        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setDeliverDays(shippingRate.getDays());
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());
        checkoutInfo.setShippingCostTotal(shippingCostTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);


        return checkoutInfo;
    }

    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {

        float shippingCostTotal = 0;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
            float finalWeight = Math.max(product.getWeight(), dimWeight);
            float shippingCost = finalWeight * item.getQuantity() * shippingRate.getRate();
            item.setShippingCost(shippingCost);
            shippingCostTotal += shippingCost;
        }

        return shippingCostTotal;
    }

    private float calcualteProductTotal(List<CartItem> cartItems) {
//      float cost = cartItems.stream().collect()
        float cost = 0.0f;
        for (CartItem cartItem : cartItems) {
            cost += cartItem.getSubtotal();
        }
        return cost;

    }

    private float calculateProductCost(List<CartItem> cartItems) {

       /* float cost = cartItems.stream().mapToLong(c -> (long)
                (c.getQuantity() * c.getProduct().getCost())).sum();

        */
        float cost = 0.0f;
        for (CartItem cartItem : cartItems) {
            cost += cartItem.getQuantity() * cartItem.getProduct().getCost();
        }
        return cost;
    }
}
