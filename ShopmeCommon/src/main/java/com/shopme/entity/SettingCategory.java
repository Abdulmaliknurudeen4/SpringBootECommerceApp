package com.shopme.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "settings_category")
public class SettingCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category", unique = true, length = 128)
    private String category;

    public SettingCategory(String category) {
        this.category = category;
    }

    public SettingCategory() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettingCategory that = (SettingCategory) o;
        return id.equals(that.id) && category.equals(that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category);
    }
}
