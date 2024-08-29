package com.shopme.checkout;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.cart.ShoppingCartController;
import com.shopme.cart.ShoppingCartService;
import com.shopme.checkout.paypal.PayPalApiException;
import com.shopme.checkout.paypal.PayPalService;
import com.shopme.customer.CustomerService;
import com.shopme.entity.Address;
import com.shopme.entity.CartItem;
import com.shopme.entity.Customer;
import com.shopme.entity.ShippingRate;
import com.shopme.entity.order.Order;
import com.shopme.entity.order.PaymentMethod;
import com.shopme.order.OrderService;
import com.shopme.settings.CurrencySettingBag;
import com.shopme.settings.EmailSettingBag;
import com.shopme.settings.PaymentSettingBag;
import com.shopme.settings.SettingService;
import com.shopme.shipping.ShippingRateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired private CheckoutService checkoutService;
    @Autowired private ControllerHelper controllerHelper;
    @Autowired private AddressService addressService;
    @Autowired private ShippingRateService shipService;
    @Autowired private ShoppingCartService cartService;
    @Autowired private OrderService orderService;
    @Autowired private SettingService settingService;
    @Autowired private PayPalService payPalService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request){

        Customer authenticatedCustomer = controllerHelper.getAuthenticatedCustomer(request);

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

        String currencyCode = settingService.getCurrencyCode();
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String paypalClientId = paymentSettings.getClientID();

        model.addAttribute("paypalClientId", paypalClientId);
        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("customer", authenticatedCustomer);
        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);


        return "checkout/checkout";
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        String paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);

        //get default address
        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if(defaultAddress != null){
            // there's a default address in the address book
            shippingRate = shipService.getShippingRateForAddress(defaultAddress);
        }else{
            // no address in the address book, using address tied to the customer object
            shippingRate = shipService.getShippingRateForCustomer(customer);
        }


        List<CartItem> cartItems = cartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckOut(cartItems, shippingRate);
        Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        cartService.deleteByCustomer(customer);
        sendOrderConfirmationEmail(request, createdOrder);
        return "checkout/order_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = order.getCustomer().getEmail();
        String subject = emailSettings.getOrderConfirmationSubject();
        String content = emailSettings.getOrderConfirmationContent();

        subject = subject.replace("[[orderId]]", emailSettings.getSenderName());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);


        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
        String orderTime = dateFormatter.format(order.getOrderTime());

        CurrencySettingBag currencySettingBag = settingService.getCurrencySettings();
        String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettingBag);

       content = content
               .replace("[[name]]", order
                       .getCustomer().getFullName())
               .replace("[[orderId]]", String.valueOf(order.getId()))
               .replace("[[orderTime]]", orderTime)
               .replace("[[shippingAddress]]", order.getShippingAddress())
               .replace("[[total]]", totalAmount)
               .replace("[[paymentMethod]]", order.getPaymentMethod().toString());

       helper.setText(content, true);
       mailSender.send(message);
    }

    @PostMapping("/process_paypal_order")
    public String processPayPalOrder(HttpServletRequest request, Model model){
        String orderId = request.getParameter("orderId");
        String pageTitle = "Checkout Failure";
        String message = null;

        try{
            if(payPalService.validateOrder(orderId)){
                return placeOrder(request);
            }else {
                pageTitle = "Checkout Failure";
                message="ERROR: Transaction could not be completed because order information is invalid";
            }
        }catch (PayPalApiException | MessagingException | UnsupportedEncodingException e){
            message = "ERROR : Transaction failed due to error: " + e.getMessage();
        }
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("message", message);

        return "message";
    }
}
