package com.shopme.admin.setting.state;

import com.shopme.entity.Country;
import com.shopme.entity.State;
import jakarta.persistence.EntityManager;
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
public class StateRepositoryTest {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void saveStates() {
        Country nigeria = entityManager.find(Country.class, 4);
        State ibandan = new State();
        ibandan.setName("Ibadan");
        ibandan.setCountry(nigeria);

        State Lagos = new State();
        Lagos.setName("Lagos");
        Lagos.setCountry(nigeria);

        assertThat(stateRepository.saveAll(List.of(ibandan, Lagos))).isNotEmpty();

    }

    @Test
    public void getStatesByCountry() {
        Country nigeria = entityManager.find(Country.class, 4);
        List<State> statesInNigeria = stateRepository.findByCountryOrderByNameAsc(nigeria);
        statesInNigeria.forEach(System.out::println);
        assertThat(statesInNigeria.size()).isEqualTo(2);
    }
}
