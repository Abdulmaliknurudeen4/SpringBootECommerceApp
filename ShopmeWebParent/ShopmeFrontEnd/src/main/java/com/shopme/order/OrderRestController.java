package com.shopme.order;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.customer.CustomerNotFoundExcpetion;
import com.shopme.customer.CustomerService;
import com.shopme.entity.Customer;
import com.shopme.entity.order.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {

    @Autowired private OrderService orderService;
    @Autowired private ControllerHelper controllerHelper;

    @PostMapping("/orders/return")
    public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
                                                      HttpServletRequest request){
        System.out.println("Order ID: " + returnRequest.getOrderId());
        System.out.println("Reason: " + returnRequest.getReason());
        System.out.println("Note: " + returnRequest.getNote());
        Customer customer = null;
        customer = controllerHelper.getAuthenticatedCustomer(request);

        try{
            orderService.setOrderReturnRequested(returnRequest, customer);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);

    }

}
