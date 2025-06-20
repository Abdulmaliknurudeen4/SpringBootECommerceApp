package com.shopme.entity;

import com.shopme.common.Constants;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends IdBasedEntity implements Serializable{

    // many to many, joincolumn refers to the primary key of users, inverse join
    // refers to Roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    public Set<Role> roles = new HashSet<Role>();
    @Column(length = 128, nullable = false, unique = true)
    private String email;
    @Column(length = 64, nullable = false)
    private String password;
    @Column(name = "first_name", length = 45, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 45, nullable = false)
    private String lastName;
    @Column(length = 64)
    private String photos;
    private boolean enabled;

    public User() {

    }

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, enabled, firstName, id, lastName, password, photos, roles);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return Objects.equals(email, other.email) && enabled == other.enabled
                && Objects.equals(firstName, other.firstName) && Objects.equals(id, other.id)
                && Objects.equals(lastName, other.lastName) && Objects.equals(password, other.password)
                && Objects.equals(photos, other.photos) && Objects.equals(roles, other.roles);
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", password=" + password + ", firstName=" + firstName
                + ", lastName=" + lastName + ", photos=" + photos + ", enabled=" + enabled + ", roles=" + roles + "]";
    }

    @Transient
    public String getPhotosImagePath() {
        if (id == null || photos == null)
            return "/images/ShopmeAdminSmall.png";

        return Constants.S3_BASE_URI+"/user-photos/" + this.id + "/" + this.photos;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public boolean hasRole(String roleName) {
        // need to debug this. it's return false even when the user is a salesperson. wrong lambda
//        return roles.stream().anyMatch(role -> role.getName().equals(roleName));
        for (Role role : roles) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;

    }

}
