package com.shopme.address;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.customer.CustomerService;
import com.shopme.entity.Address;
import com.shopme.entity.Country;
import com.shopme.entity.Customer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Controller
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private ControllerHelper controllerHelper;

    @GetMapping("/address_book")
    public String showAddressBook(Model model, HttpServletRequest request) {
        Customer authenticatedCustomer = controllerHelper.getAuthenticatedCustomer(request);
        List<Address> addresses = addressService.listAddressBook(authenticatedCustomer);

        boolean usePrimaryAddressAsDefault = true;
        for (Address address : addresses) {
            if (address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false;
                break;
            }
        }

        model.addAttribute("listAddresses", addresses);
        model.addAttribute("customer", authenticatedCustomer);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        return "address_book/addresses";
    }

    @GetMapping("/address_book/new")
    public String newAddress(Model model) {
        List<Country> listCountries = customerService.listAllCountry();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Add New Address");

        return "address_book/address_form";
    }

    @PostMapping("/address_book/save")
    public String saveAddress(Address address, HttpServletRequest request,
                              RedirectAttributes ra) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        address.setCustomer(customer);
        addressService.save(address);

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";

        if("cart".equals(redirectOption)){
            redirectURL+="?redirect=cart";
        }
        if("checkout".equals(redirectOption)){
            redirectURL+="?redirect=checkout";
        }


        ra.addFlashAttribute("message", "The address has been saved successfully.");

        return redirectURL;
    }

    @GetMapping("/address_book/edit/{id}")
    public String editAddress(@PathVariable("id") Integer addressId, Model model,
                              HttpServletRequest request) {

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        List<Country> listCountries = customerService.listAllCountry();

        Address address = addressService.get(addressId, customer.getId());

        model.addAttribute("address", address);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Edit Address (ID: " + address.getId() + ")");

        return "address_book/address_form";

    }

    @GetMapping("/address_book/delete/{id}")
    public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra,
                                HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        addressService.delete(addressId, customer.getId());

        ra.addFlashAttribute("message", "The address ID " + addressId + " has been deleted.");

        return "redirect:/address_book";
    }

    @GetMapping("/address_book/default/{id}")
    public String setDefaultAddress(@PathVariable("id") Integer addressId,
                                    HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        addressService.setDefaultAddress(addressId, customer.getId());

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";

        if("cart".equals(redirectOption)){
            redirectURL="redirect:/cart";
        }
        if("checkout".equals(redirectOption)){
            redirectURL="redirect:/checkout";
        }

        return redirectURL;
    }
}
