package com.shopme.admin.setting.country;

import com.shopme.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CountryRepositoryTest {
    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void createCountries() {
        assertThat(countryRepository.saveAll(
                List.of(
                        new Country("Nigeria", "NGN"),
                        new Country("China", "CN"),
                        new Country("Qatar", "QTR")
                )
        )).isNotNull();
    }

    @Test
    public void printAllCountries(){
        countryRepository.findAllByOrderByNameAsc().forEach(country -> System.out.println(country));
        assertThat(true).isTrue();
    }

    @Test
    public void deleteCountry(){
        countryRepository.findAllByOrderByNameAsc()
                .stream()
                .filter(c -> c.getName().equals("China"))
                .forEach(c -> countryRepository.delete(c));
        assertThat(true).isTrue();
    }




}
