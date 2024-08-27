package com.shopme.admin.review;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 5;

    @Autowired
    private ReviewRepository repo;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, REVIEWS_PER_PAGE, repo);
    }

    public Review get(Integer id) throws ReviewNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ReviewNotFoundException("Couldn't find any reviews with ID" + id);
        }
    }

    public void save(Review reviewInForm) {
        Review reviewInDb = repo.findById(reviewInForm.getId()).get();
        reviewInDb.setHeadline(reviewInForm.getHeadline());
        reviewInDb.setComment(reviewInForm.getComment());

        repo.save(reviewInDb);
    }

    public void delete(Integer id) throws ReviewNotFoundException {
        if (!repo.existsById(id)) {
            throw new ReviewNotFoundException("Could not find any reviews with ID " + id);
        }
        repo.deleteById(id);
    }
}
