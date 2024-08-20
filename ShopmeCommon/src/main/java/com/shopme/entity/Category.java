package com.shopme.entity;

import com.shopme.common.Constants;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category extends IdBasedEntity implements Serializable{

    @Column(length = 128, nullable = false, unique = true)
    private String name;
    @Column(length = 128, nullable = false, unique = true)
    private String alias;

    @Column(length = 64, nullable = true)
    private String photo;
    private boolean enabled;

    @Column(name = "all_parent_ids", length = 255, nullable = true)
    private String allParentIDs;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @OrderBy("name asc")
    private Set<Category> children = new HashSet<>();

    public Category() {
    }

    public Category(Integer id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Category(String name) {
        this.name = name;
        this.alias = name;
        this.photo = "default.png";
    }

    public Category(String name, Category parent) {
        this.name = name;
        this.alias = name;
        this.photo = "default.png";
        this.parent = parent;
    }

    public Category(String name, String alias, String photo) {
        this.name = name;
        this.alias = alias;
        this.photo = photo;
    }

    public static Category copyIdAndName(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        return copyCategory;
    }

    public static Category copyIdAndName(Integer id, String name) {
        Category copyCategory = new Category();
        copyCategory.setId(id);
        copyCategory.setName(name);
        return copyCategory;
    }

    public static Category copyFull(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setPhoto(category.getPhoto());
        copyCategory.setEnabled(category.isEnabled());
        return copyCategory;
    }

    public static Category copyFull(Category category, String name) {
        Category copyCategory = Category.copyFull(category);
        copyCategory.setName(name);
        return copyCategory;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id.equals(category.id) && name.equals(category.name) && alias.equals(category.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, alias);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", enabled=" + enabled +
                '}';
    }

    @Transient
    public String getPhotosImagePath() {
        if (id == null || photo == null)
            return "/images/ShopmeAdminSmall.png";
        return Constants.S3_BASE_URI+"/categories-images/" + this.id + "/" + this.photo;
    }

    public boolean hasChildren() {
        return !(this.getChildren().size() == 0 || this.getChildren() == null);
    }

    public String getAllParentIDs() {
        return allParentIDs;
    }

    public void setAllParentIDs(String allParentIDs) {
        this.allParentIDs = allParentIDs;
    }
}

