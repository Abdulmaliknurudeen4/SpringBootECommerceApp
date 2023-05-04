package com.shopme.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "state")
public class State {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @Column(nullable = false, length = 45)
    private String name;

    @ManyToOne
    @JsonBackReference
    private Country country;

    public State(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public State() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(Id, state.Id) && Objects.equals(name, state.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, name);
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "State: " + "Id=" + Id + ", name='" + name;
    }
}
