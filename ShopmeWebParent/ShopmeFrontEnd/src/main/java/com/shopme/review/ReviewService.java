package com.shopme.review;

import com.shopme.entity.Customer;
import com.shopme.entity.Review;
import com.shopme.entity.order.OrderStatus;
import com.shopme.entity.product.Product;
import com.shopme.order.OrderDetailRepository;
import com.shopme.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 5;

    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private OrderDetailRepository orderDetailRepo;
    @Autowired
    private ProductRepository productRepo;

    public Page<Review> listByCustomerByPage(Customer customer, String keyword, int pageNum,
                                             String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

        if (keyword != null) {
            return reviewRepo.findByCustomer(customer.getId(), keyword, pageable);
        }

        return reviewRepo.findByCustomer(customer.getId(), pageable);
    }

    public Review getByCustomerAndId(Customer customer, Integer reviewId) throws ReviewNotFoundExcpetion {
        Review review = reviewRepo.findByCustomerAndId(customer.getId(), reviewId);
        if (review == null)
            throw new ReviewNotFoundExcpetion("Customer doesn not have any reviews with ID " + reviewId);

        return review;
    }

    public Page<Review> list3MostRecentlyReviewsByProduct(Product product) {
        Sort sort = Sort.by("reviewTime").descending();
        Pageable pageable = PageRequest.of(0, 3, sort);

        return reviewRepo.findByProduct(product, pageable);
    }

    public Page<Review> list3MostVotedByReviewsByProduct(Product product) {
        Sort sort = Sort.by("votes").descending();
        Pageable pageable = PageRequest.of(0, 3, sort);

        return reviewRepo.findByProduct(product, pageable);
    }

    public Page<Review> listByProduct(Product product, int pageNum, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

        return reviewRepo.findByProduct(product, pageable);
    }

    public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
        Long count = reviewRepo.countByCustomerAndProduct(customer.getId(), productId);
        return count > 0.0;
    }

    public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
        Long count = orderDetailRepo.countByProductAndCustomerAndOrderStatus(productId, customer.getId(), OrderStatus.DELIVERED);
        return count > 0;
    }

    public Review save(Review review) {
        review.setReviewTime(new Date());
        Review savedReview = reviewRepo.save(review);

        Integer productId = savedReview.getProduct().getId();
        productRepo.updateReviewCountAndAverageRating(productId);

        return savedReview;
    }
}
