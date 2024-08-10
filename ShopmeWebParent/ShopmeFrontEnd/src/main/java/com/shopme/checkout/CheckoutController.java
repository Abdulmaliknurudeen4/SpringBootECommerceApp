package com.shopme.checkout;

import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.cart.ShoppingCartController;
import com.shopme.cart.ShoppingCartService;
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
public class CheckoutController {

    @Autowired private CheckoutService checkoutService;
    @Autowired private CustomerService customerService;
    @Autowired private AddressService addressService;
    @Autowired private ShippingRateService shipService;
    @Autowired private ShoppingCartService cartService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request){

        Customer authenticatedCustomer = getAuthenticatedCustomer(request);

        //get default address
        Address defaultAddress = addressService.getDefaultAddress(authenticatedCustomer);
        ShippingRate shippingRate = null;

        if(defaultAddress != null){
            // there's a default address in the address book
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shipService.getShippingRateForAddress(defaultAddress);
        }else{
            // no address in the address book, using address tied to the customer object
            model.addAttribute("shippingAddress", authenticatedCustomer.toString());
            shippingRate = shipService.getShippingRateForCustomer(authenticatedCustomer);
        }

        if(shippingRate == null){
            return "redirect:/cart";
        }
        List<CartItem> cartItems = cartService.listCartItems(authenticatedCustomer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckOut(cartItems, shippingRate);

        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);


        return "checkout/checkout";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticationUser(request);
        // The email is always not null.
        return customerService.getCustomerByEmail(email);
    }
}
