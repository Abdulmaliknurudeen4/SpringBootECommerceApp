package com.shopme.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "addresses")
public class Address extends AbstractAddressWithCountry {


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "default_address")
    private boolean defaultForShipping;



    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean isDefaultForShipping() {
        return defaultForShipping;
    }

    public void setDefaultForShipping(boolean defaultForShipping) {
        this.defaultForShipping = defaultForShipping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return defaultForShipping == address.defaultForShipping && Objects.equals(id, address.id) && Objects.equals(firstName, address.firstName) && Objects.equals(lastName, address.lastName) && Objects.equals(phoneNumber, address.phoneNumber) && Objects.equals(addressLine1, address.addressLine1) && Objects.equals(addressLine2, address.addressLine2) && Objects.equals(city, address.city) && Objects.equals(getCountry(), address.getCountry()) && Objects.equals(state, address.state) && Objects.equals(postalCode, address.postalCode) && Objects.equals(customer, address.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, phoneNumber, addressLine1, addressLine2, city, getCountry(), state, postalCode, customer, defaultForShipping);
    }


}
