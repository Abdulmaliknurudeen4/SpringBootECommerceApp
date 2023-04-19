package com.shopme.admin.setting.country;

import com.shopme.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;

    public List<Country> countryList() {
        List<Country> countries = new ArrayList<>(countryRepository.findAllByOrderByNameAsc());
        return countries;
    }

    public Country save(Country country) {
        if (country != null) {
            Country save = countryRepository.save(country);
            return save;
        }
        return null;
    }

    public void delete(Integer id) {
        countryRepository.deleteById(id);
    }
}
