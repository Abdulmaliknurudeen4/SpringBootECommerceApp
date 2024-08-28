package com.shopme.admin.review;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.product.ProductRepository;
import com.shopme.entity.Review;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Transactional
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 5;

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ProductRepository productRepository;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, REVIEWS_PER_PAGE, reviewRepository);
    }

    public Review get(Integer id) throws ReviewNotFoundException {
        try {
            return reviewRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ReviewNotFoundException("Couldn't find any reviews with ID" + id);
        }
    }

    public void save(Review reviewInForm) {
        Review reviewInDb = reviewRepository.findById(reviewInForm.getId()).get();
        reviewInDb.setHeadline(reviewInForm.getHeadline());
        reviewInDb.setComment(reviewInForm.getComment());

        reviewRepository.save(reviewInDb);
        productRepository.updateReviewCountAndAverageRating(reviewInDb.getProduct().getId());
    }

    public void delete(Integer id) throws ReviewNotFoundException {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Could not find any reviews with ID " + id);
        }
        reviewRepository.deleteById(id);
    }
}
