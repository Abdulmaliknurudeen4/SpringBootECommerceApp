package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.setting.SettingService;
import com.shopme.entity.Country;
import com.shopme.entity.order.*;
import com.shopme.entity.product.Product;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

@Controller
public class OrderController {
    private final String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingService settingService;

    @GetMapping("/orders")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listOrders",
                    moduleURL = "/orders", contextDisplay = "Order(s)") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum,
            HttpServletRequest request,
            @AuthenticationPrincipal ShopmeUserDetails loggedUser) {

        orderService.listByPage(pageNum, helper);
        loadCurrencySetting(request);

        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
            return "orders/orders_shipper";
        }

        return "orders/orders";

    }

    private void loadCurrencySetting(HttpServletRequest request) {
        settingService.getCurrencySettings()
                .forEach(c -> {
                    System.out.printf("Setting Key: %s, Value of Key: %s%n%n", c.getSettingKey(), c.getValue());
                    request.setAttribute(c.getSettingKey(), c.getValue());
                });
    }

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetails(@PathVariable("id") Integer id, Model model
            , RedirectAttributes ra, HttpServletRequest request,
                                   @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
        try {
            Order order = orderService.get(id);
            loadCurrencySetting(request);

            boolean isVisibleForAdminOrSalesperson = loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson");

            model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
            model.addAttribute("order", order);
            return "orders/order_details_modal";
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            orderService.delete(id);
            ra.addFlashAttribute("message", "The order ID " + id + " has been deleted.");
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable("id") Integer id,
                            Model model, RedirectAttributes ra,
                            HttpServletRequest request) {

        try {
            Order order = orderService.get(id);
            List<Country> listCountries = orderService.listAllCountries();

            model.addAttribute("pageTitle", "Edit Order (ID: " + id + " )");
            model.addAttribute("order", order);
            model.addAttribute("listCountries", listCountries);

            return "orders/order_form";
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }

    }

    @PostMapping("/orders/save")
    public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
        String countryName = request.getParameter("countryName").trim();
        order.setCountry(countryName);

        updateProductDetails(order, request);
        updateOrderTracks(order, request);

        orderService.save(order);
        ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully.");

        return defaultRedirectURL;
    }

    private void updateOrderTracks(Order order, HttpServletRequest request) {
        String[] trackIds = request.getParameterValues("trackId");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackNotes = request.getParameterValues("trackNotes");

        List<OrderTrack> orderTracks = order.getOrderTracks();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        if (trackIds == null) {
        } else {
            for (int i = 0; i < trackIds.length; i++) {
                OrderTrack trackRecord = new OrderTrack();

                int trackId = Integer.parseInt(trackIds[i]);
                if (trackId > 0) {
                    trackRecord.setId(trackId);
                }

                trackRecord.setOrder(order);
                trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
                trackRecord.setNotes(trackNotes[i].trim());

                try {
                    dateFormatter.parse(trackDates[i]);
                    trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                orderTracks.add(trackRecord);
                order.setOrderTracks(orderTracks);

            }
        }


    }

    private void updateProductDetails(Order order, HttpServletRequest request) {
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productDetailCosts = request.getParameterValues("productDetailCost");
        String[] productQuantities = request.getParameterValues("productQuantity");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShipCosts = request.getParameterValues("productShipCost");

        Set<OrderDetail> orderDetails = order.getOrderDetail();

        for (int i = 0; i < detailIds.length; i++) {
            System.out.println("Detail ID: " + detailIds[i]);
            System.out.println("\t Product ID: " + productIds[i]);
            System.out.println("\t Cost: " + productDetailCosts[i]);
            System.out.println("\t Price: " + productPrices[i]);
            System.out.println("\t Quantity: " + productQuantities[i]);
            System.out.println("\t Subtotal: " + productSubtotals[i]);
            System.out.println("\t Ship Cost: " + productShipCosts[i]);

            OrderDetail orderDetail = new OrderDetail();
            int detailId = Integer.parseInt(detailIds[i]);
            if (detailId > 0) {
                orderDetail.setId(detailId);
            }

            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
            orderDetail.setQuality(Integer.parseInt(productQuantities[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

            orderDetails.add(orderDetail);

        }
    }
}
