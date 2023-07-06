package com.shopme.cart;

import com.shopme.Utility;
import com.shopme.customer.CustomerNotFoundExcpetion;
import com.shopme.customer.CustomerService;
import com.shopme.entity.CartItem;
import com.shopme.entity.Customer;
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

    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request){
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = shoppingCartService.listCartItems(authenticatedCustomer);
        float estimatedtotal = 0;
        for(CartItem i : cartItems){
            estimatedtotal += i.getSubtotal();
        }

//                OptionalDouble allocationsToEmployee = allocationService.getAllocations()
//                .stream()
//                .filter(f -> f.getEmployeeID().equals(employee.getId()))
//                .map(e -> Duration.between(e.getStartTime(), e.getEndTime()).toHours())
//                .mapToLong(Long::longValue).average();

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
