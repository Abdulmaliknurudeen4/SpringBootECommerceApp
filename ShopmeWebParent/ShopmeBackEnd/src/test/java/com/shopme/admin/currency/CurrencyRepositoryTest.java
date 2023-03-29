package com.shopme.admin.currency;

import com.shopme.entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRespository currencyRespository;
    @Autowired
    private TestEntityManager entity;

    @Test
    public void testCreateCategories() {
        List<Currency> currencies = List.of(
                new Currency("United States Dollar", "$", "USD"),
                new Currency("British Pound", "£", "GBP"),
                new Currency("Japanese Yen", "¥", "JPY"),
                new Currency("Euro", "€", "EUR"),
                new Currency("Russian Ruble", "₽", "RUB"),
                new Currency("South Korean Won", "₩", "KRW"),
                new Currency("Chinese Yuan", "¥", "CNY"),
                new Currency("Brazilian Real", "R$", "BRL"),
                new Currency("Australian Dollar", "$", "AUD"),
                new Currency("Canadian Dollar", "$", "CAD"),
                new Currency("Vietnamese Dong", "d", "VND"),
                new Currency("Indian Rupee", "₹", "INR"),
                new Currency("Nigerian Naira", "₦", "NGN")
        );
        Iterable<Currency> currencies1 = currencyRespository.saveAll(currencies);
        assertThat(currencies1.iterator()).isNotNull();
    }

}
