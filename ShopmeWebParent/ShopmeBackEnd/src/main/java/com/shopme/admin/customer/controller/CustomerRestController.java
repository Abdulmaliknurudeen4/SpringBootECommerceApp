package com.shopme.admin.customer.controller;

import com.shopme.admin.customer.CustomerService;
import com.shopme.admin.setting.country.CountryService;
import com.shopme.admin.setting.state.StateRepository;
import com.shopme.entity.Country;
import com.shopme.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class CustomerRestController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CountryService service;

    @Autowired
    private StateRepository stateRepository;


    @PostMapping("customers/check_email")
    public String checkDuplicateEmail(@Param("email") String email, @Param("id") Integer id) {
        return customerService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }

    @GetMapping("customers/list_state_by_country/{country_id}")
    public List<State> listStateForCountry(@PathVariable("country_id") Integer countryId) {
        Optional<Country> country = service.getCountry(countryId);
        return country.map(value -> stateRepository.findByCountryOrderByNameAsc(value)).orElse(null);
    }
}
