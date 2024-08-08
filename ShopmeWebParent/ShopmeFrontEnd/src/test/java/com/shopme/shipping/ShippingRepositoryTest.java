package com.shopme.shipping;

import com.shopme.entity.Country;
import com.shopme.entity.ShippingRate;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShippingRepositoryTest {

    @Autowired
    private ShippingRateRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void tesFindByCountryAndState(){
        Country usa = entityManager.find(Country.class, 234);
        String state = "New York";
        ShippingRate byCountryAndState = repo.findByCountryAndState(usa, state);
        assertThat(byCountryAndState).isNotNull();
        System.out.println(byCountryAndState);
    }

}
