package com.shopme.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(length = 40, nullable = false, unique = true)
    private String name;

    @Column(length = 140, nullable = false)
    private String description;

    public Role() {
    }

    public Role(Integer id) {
        this.Id = id;
    }

    public Role(String name) {
        super();
        this.name = name;
    }

    public Role(String name, String description) {
        super();
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
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
        return Objects.equals(Id, other.Id);
    }

    @Override
    public String toString() {
        return this.description;
    }

}
