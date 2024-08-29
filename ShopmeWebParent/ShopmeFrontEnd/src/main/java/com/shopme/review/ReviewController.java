package com.shopme.review;

import com.shopme.Utility;
import com.shopme.customer.CustomerService;
import com.shopme.entity.Customer;
import com.shopme.entity.Review;
import com.shopme.entity.product.Product;
import com.shopme.exception.ProductNotFoundException;
import com.shopme.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ReviewController {

    private final String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;

    @GetMapping("/reviews")
    public String listFirstPage(Model model) {
        return defaultRedirectURL;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listReviewsByCustomerByPage(Model model, HttpServletRequest request,
                                              @PathVariable(name = "pageNum") int pageNum,
                                              String keyword, String sortField, String sortDir) {
        Customer customer = getAuthenticatedCustomer(request);
        Page<Review> page = reviewService.listByCustomerByPage(customer, keyword, pageNum, sortField, sortDir);
        List<Review> listReviews = page.getContent();

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("moduleURL", "/reviews");
        model.addAttribute("contextDisplay", "review(s)");

        model.addAttribute("listReviews", listReviews);

        long startCount = (long) (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
        model.addAttribute("startCount", startCount);

        long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("endCount", endCount);

        return "reviews/reviews_customer";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticationUser(request);
        // The email is always not null.
        return customerService.getCustomerByEmail(email);
    }

    @GetMapping("/reviews/detail/{id}")
    public String viewReview(@PathVariable("id") Integer id, Model model,
                             RedirectAttributes ra, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        try {
            Review review = reviewService.getByCustomerAndId(customer, id);
            model.addAttribute("review", review);

            return "reviews/review_detail_modal";
        } catch (ReviewNotFoundExcpetion ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/ratings/{productAlias}/page/{pageNum}")
    public String listByProduct(Model model,
                                @PathVariable("productAlias") String productAlias,
                                @PathVariable("pageNum") int pageNum,
                                String sortField, String sortDir) {
        Product product = null;
        try {
            product = productService.getProduct(productAlias);
        } catch (ProductNotFoundException exception) {
            return "error/404";
        }
        Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
        List<Review> listReviews = page.getContent();

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("contextDisplay", "review(s)");
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listReviews", listReviews);
        model.addAttribute("product", product);

        long startCount = (long) (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE;
        model.addAttribute("startCount", startCount);

        long endCount = startCount + ReviewService.REVIEWS_PER_PAGE;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("endCount", endCount);
        model.addAttribute("pageTitle", "Reviews for " + product.getShortName());

        return "reviews/reviews_product";
    }

    @GetMapping("/ratings/{productAlias}")
    public String listByProductFirstPage(@PathVariable(name = "productAlias") String productAlias, Model model) {
        return listByProduct(model, productAlias, 1, "reviewTime", "desc");
    }
    @GetMapping("/write_review/product/{productId}")
    public String showViewForm(@PathVariable("productId") Integer productId, Model model, HttpServletRequest request){
        Review review = new Review();
        Product product = null;
        try {
            product = productService.getProductById(productId);
        }catch (ProductNotFoundException e){
            return "error/404";
        }
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        boolean customerReviewed = reviewService.didCustomerReviewProduct(authenticatedCustomer, product.getId());
        if(customerReviewed){
            model.addAttribute("customerReviewed", customerReviewed);
        }else{
            boolean customerCanReview = reviewService.canCustomerReviewProduct(authenticatedCustomer, product.getId());
            if(customerCanReview) {
                model.addAttribute("customerCanReview", customerCanReview);
            }else {
                model.addAttribute("NoReviewPermission", true);
            }
        }
        model.addAttribute("product", product);
        model.addAttribute("review", review);

        return "reviews/reviews_form";
    }

}
