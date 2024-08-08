package com.shopme.cart;

import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.customer.CustomerService;
import com.shopme.entity.Address;
import com.shopme.entity.CartItem;
import com.shopme.entity.Customer;
import com.shopme.entity.ShippingRate;
import com.shopme.shipping.ShippingRateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ShippingRateService shippingRateService;
    @Autowired
    private AddressService addressService;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = shoppingCartService.listCartItems(authenticatedCustomer);

        float estimatedtotal = 0;
        for (CartItem i : cartItems) {
            estimatedtotal += i.getSubtotal();
        }

        //get default address
        Address defaultAddress = addressService.getDefaultAddress(authenticatedCustomer);
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        if(defaultAddress != null){
            // there's a default address in the address book
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        }else{
            // no address in the address book, using address tied to the customer object
            usePrimaryAddressAsDefault = true;
            shippingRate = shippingRateService.getShippingRateForCustomer(authenticatedCustomer);
        }


        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate !=null);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedtotal);

        return "cart/shopping_cart";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticationUser(request);
        // The email is always not null.
        return customerService.getCustomerByEmail(email);
    }
}
