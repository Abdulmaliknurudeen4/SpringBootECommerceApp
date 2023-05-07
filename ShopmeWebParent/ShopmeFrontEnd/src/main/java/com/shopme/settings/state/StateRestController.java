package com.shopme.settings.state;


import com.shopme.entity.Country;
import com.shopme.entity.State;
import com.shopme.settings.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class StateRestController {

    @Autowired
    private StateRepository repo;

    @Autowired
    private CountryRepository countryRepo;

    @GetMapping("/settings/list_state_by_country/{country_id}")
    public List<State> listByCountry(@PathVariable("country_id") Integer countryId) {
        Optional<Country> country = countryRepo.findById(countryId);
        return country.map(value -> repo.findByCountryOrderByNameAsc(value)).orElse(null);
    }
}
