package com.shopme.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.Serializable;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements Serializable {

    @Autowired
    public ShopmeUserDetialService userDetialService;

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests()
                .requestMatchers("/states/list_by_country/**").hasAnyAuthority("Admin", "Salesperson")
                .requestMatchers("/users/**", "/settings/**").hasAuthority("Admin")
                .requestMatchers("/questions/**", "/reviews/**").hasAnyAuthority("Admin", "Assistant")
                .requestMatchers("/categories/**", "/brands/**", "/articles/**", "/menus/**").hasAnyAuthority("Admin", "Editor")
                .requestMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
                .requestMatchers("/products/edit/**", "/products/save", "/products/check_unique").hasAnyAuthority("Admin", "Editor", "Salesperson")
                .requestMatchers("/products", "/products/", "/products/detials/**", "/products/page/**").hasAnyAuthority("Admin", "Editor","Salesperson", "Shipper")
                .requestMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
                .requestMatchers("/orders/**", "/customers/**", "/Shipping/**", "/get_shipping_cost", "/reports/**").hasAnyAuthority("Admin", "Salesperson")
                .requestMatchers("/orders/**").hasAuthority("Shipper")
                .requestMatchers("/orders_shipper/update/**").hasAuthority("Shipper")
                .requestMatchers("/reviews/**").hasAnyAuthority("Admin", "Assistant")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .rememberMe()
                .key("ADASDFKJLJLDFJoijklajdflkajd_1233k##kl")
                .tokenValiditySeconds(7 * 60 * 60 * 24)
                .and()
                //enable iframe form the same origin
                .headers().frameOptions().sameOrigin()
                .and()
                .build();

    }

  /*  @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) ->
                        authz.a
                        .antMatchers("/api/admin/**").hasRole("ADMIN")
                        .antMatchers("/api/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                );
        return http.build();
    }*/

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(false)
                .ignoring()
                .requestMatchers("/images/**", "/js/**", "/webjars/**", "/styles/**");
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetialService);
        authProvider.setPasswordEncoder(PasswordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder bCryptPasswordEncoder, ShopmeUserDetialService userDetailService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }
}
