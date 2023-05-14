package com.shopme.security.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomerOAuthUser implements OAuth2User {

    private final OAuth2User oAuth2User;
    private final String clientName;
    private String fullName;

    public CustomerOAuthUser(OAuth2User oauth2User, String clientName) {

        this.oAuth2User = oauth2User;
        this.clientName = clientName;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getAttribute("name");
    }

    public String getEmail() {
        return oAuth2User.getAttribute("email");
    }

    public String getFullname() {
        return (fullName != null) ? fullName : oAuth2User.getAttribute("name");
    }

    public String getClientName() {
        return clientName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
