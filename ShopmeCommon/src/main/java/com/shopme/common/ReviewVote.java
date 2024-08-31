package com.shopme.common;

import com.shopme.entity.Customer;
import com.shopme.entity.IdBasedEntity;
import com.shopme.entity.Review;
import com.shopme.entity.Role;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "reviews_vote")
public class ReviewVote extends IdBasedEntity {
    private static final int VOTE_UP_POINT = 1;
    private static final int VOTE_DOWN_POINT =2;

    private int votes;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
    public void voteUp(){
        this.votes = VOTE_UP_POINT;
    }
    public void voteDown(){
        this.votes = VOTE_DOWN_POINT;
    }

    @Override
    public String toString() {
        return "ReviewVote{" +
               "votes=" + votes +
               ", customer=" + customer +
               ", review=" + review +
               '}';
    }
    @Transient
    public boolean isUpvoted() {
        return this.votes == VOTE_UP_POINT;
    }

    @Transient
    public boolean isDownvoted() {
        return this.votes == VOTE_DOWN_POINT;
    }

    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Role other = (Role) obj;
        return Objects.equals(id, other.getId());
    }
}
