package com.shopme.admin.setting.country;

import com.shopme.admin.setting.state.StateRepository;
import com.shopme.entity.Country;
import com.shopme.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CountryRestController {
    @Autowired
    private CountryService service;

    @GetMapping("/countries/list")
    public List<Country> listAll() {
        return service.countryList();
    }

    @PostMapping("/countries/save")
    public String save(@RequestBody Country country) {
        Country savedCountry = service.save(country);
        return String.valueOf(savedCountry.getId());
    }

    @GetMapping("/countries/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Integer id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted");
    }


}
