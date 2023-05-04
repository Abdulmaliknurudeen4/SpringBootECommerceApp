package com.shopme.admin.setting.state;

import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.entity.Country;
import com.shopme.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class StateRestController {

    @Autowired
    private StateRepository repo;

    @Autowired
    private CountryRepository countryRepo;

    @GetMapping("/states/list_by_country/{country_id}")
    public List<State> listByCountry(@PathVariable("country_id") Integer countryId) {
        Optional<Country> country = countryRepo.findById(countryId);
        return country.map(value -> repo.findByCountryOrderByNameAsc(value)).orElse(null);
    }

    @GetMapping("/states/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Integer id){
        repo.deleteById(id);
        return  ResponseEntity.ok("Deleted");
    }

    @PostMapping("/states/save")
    public String save(@RequestBody State state){
        State savedState = repo.save(state);
        return String.valueOf(savedState.getId());
    }
}
