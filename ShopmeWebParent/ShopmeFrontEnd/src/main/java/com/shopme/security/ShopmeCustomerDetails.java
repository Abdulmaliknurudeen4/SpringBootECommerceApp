package com.shopme.security;

import com.shopme.entity.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

public class ShopmeCustomerDetails implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Customer customer;

    public ShopmeCustomerDetails(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return customer.getEnabled();
    }

    public String getFullname() {
        return this.customer.getFullName();
    }

    public void setFirstName(String firstName) {
        this.customer.setFirstName(firstName);
    }

    public void setlastName(String lastname) {
        this.customer.setLastName(lastname);
    }

    public Customer getCustomer() {
        return customer;
    }
}
