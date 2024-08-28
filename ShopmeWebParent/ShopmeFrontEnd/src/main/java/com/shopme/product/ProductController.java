package com.shopme.product;

import com.shopme.category.CategoryService;
import com.shopme.entity.Category;
import com.shopme.entity.Review;
import com.shopme.entity.product.Product;
import com.shopme.exception.CategoryNotFoundException;
import com.shopme.exception.ProductNotFoundException;
import com.shopme.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
        return viewCategoryByPage(alias, model, 1, "name", "asc", null);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias, Model model,
                                     @PathVariable(name = "pageNum") int pageNum,
                                     @Param("sortField") String sortField,
                                     @Param("sortDir") String sortDir,
                                     @Param("keyword") String keyword) {
        Category category = null;
        try {
            category = categoryService.getCategory(alias);

            List<Category> listCategoryParent = categoryService.getCategoryParents(category);
            Page<Product> productPage = productService.listByCategory(pageNum, category.getId());
            List<Product> productList = productPage.getContent();

            pageNum = (pageNum <= 0) ? 0 : pageNum;

            long startCount = (long) (pageNum - 1) * ProductService.PRODUCT_PER_PAGE + 1;
            long endCount = startCount + ProductService.PRODUCT_PER_PAGE - 1;

            // Getting to the last page with uneven elements
            if (endCount > productPage.getTotalElements()) {
                endCount = productPage.getTotalElements();
            }
            String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

            model.addAttribute("currentPage", pageNum);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", productPage.getTotalElements());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", reverseSortDir);
            model.addAttribute("keyword", keyword);
            model.addAttribute("products", productList);
            model.addAttribute("category", category);


            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("listCategoryParents", listCategoryParent);
            return "product/products_by_category";
        } catch (CategoryNotFoundException e) {
            return "error/404";
        }

    }

    @GetMapping("/p/{product_alias}")
    public String viewProductDetial(@PathVariable("product_alias") String alias, Model model) {
        try {
            Product product = productService.getProduct(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
            Page<Review> listReviews = reviewService.list3MostRecentlyReviewsByProduct(product);

            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("product", product);
            model.addAttribute("listReviews", listReviews);
            model.addAttribute("pageTitle", product.getShortName());
            return "product/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/search")
    public String searchFirstPage(@Param("keyword") String keyword, Model model) {
        return searchByPage(keyword, 1, model);
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchByPage(@Param("keyword") String keyword,
                               @PathVariable("pageNum") int pageNum,
                               Model model) {
        List<Product> productList = null;
        pageNum = (pageNum <= 0) ? 0 : pageNum;
        Page<Product> productPage = productService.search(keyword, pageNum);
        productList = productPage.getContent();


        long startCount = (long) (pageNum - 1) * ProductService.PRODUCT_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCT_PER_PAGE - 1;

        // Getting to the last page with uneven elements
        if (endCount > productPage.getTotalElements()) {
            endCount = productPage.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("pageTitle", keyword + " - Search Result ");
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("products", productList);
        model.addAttribute("keyword", keyword);

        return "product/search_result";
    }
}
