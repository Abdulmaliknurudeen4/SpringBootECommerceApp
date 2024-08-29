package com.shopme.cart;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.customer.CustomerNotFoundExcpetion;
import com.shopme.customer.CustomerService;
import com.shopme.entity.Customer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartRestController {
    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private ControllerHelper controllerHelper;


    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable("productId") Integer productId,
                                   @PathVariable("quantity") Integer quantity,
                                   HttpServletRequest request) {
        try {
            Customer authenticatedCustomer = controllerHelper.getAuthenticatedCustomer(request);
            Integer addedProduct = cartService.addProduct(productId, quantity, authenticatedCustomer);
            return addedProduct + " item(s) of this product added to your shopping cart";
        } catch (ShoppingCartException e) {
            return "You cannot Add more than 5 products of this product";
        }
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateProductFromCart(@PathVariable("productId") Integer productId,
                                        @PathVariable("quantity") Integer quantity,
                                        HttpServletRequest request){
        Customer authenticatedCustomer = controllerHelper.getAuthenticatedCustomer(request);
        float subtotal = cartService.updateQuantity(productId, quantity, authenticatedCustomer);
        return String.valueOf(subtotal);
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable("productId") Integer productId, HttpServletRequest request){
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        cartService.removeProduct(productId, customer);
        return "The product has been removed from your shopping cart";
    }
}
