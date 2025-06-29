package com.shopme.order;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.customer.CustomerService;
import com.shopme.entity.Customer;
import com.shopme.entity.order.Order;
import com.shopme.entity.order.OrderDetail;
import com.shopme.entity.product.Product;
import com.shopme.review.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Iterator;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ControllerHelper controllerHelper;
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/orders")
    public String listFirstPage(Model model, HttpServletRequest request) {
        return listByPage(model, request, 1, "orderTime", "desc", null);
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(Model model, HttpServletRequest request,
                             @PathVariable(name = "pageNum") int pageNum,
                             String sortField, String sortDir, String orderKeyword) {

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        Page<Order> page = orderService.listForCustomerByPage(customer, pageNum, sortField, sortDir, orderKeyword);
        List<Order> listOrders = page.getContent();

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("orderKeyword", orderKeyword);
        model.addAttribute("moduleURL", "/orders");
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        long startCount = (long) (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
        model.addAttribute("startCount", startCount);

        long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("endCount", endCount);
        return "orders/orders_customer";


    }

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetails(Model model,
                                   @PathVariable(name = "id") Integer id,
                                   HttpServletRequest request){
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);

        Order order = orderService.getOrder(id, customer);
        
        setProductReviewableStatus(customer, order);
        
        model.addAttribute("order", order);

        return "orders/order_details_modal";
    }

    private void setProductReviewableStatus(Customer customer, Order order) {

        Iterator<OrderDetail> iterator = order.getOrderDetail().iterator();

        while (iterator.hasNext()){
            OrderDetail orderDetail = iterator.next();
            Product product = orderDetail.getProduct();
            Integer productId = product.getId();

            boolean didCustomerReviewProduct = reviewService.didCustomerReviewProduct(customer, productId);
            product.setReviewedByCustomer(didCustomerReviewProduct);

            if(!didCustomerReviewProduct){
                boolean canCustomerReviewProduct = reviewService.canCustomerReviewProduct(customer, productId);
                product.setCustomerCanReview(canCustomerReviewProduct);
            }
        }
    }


}
