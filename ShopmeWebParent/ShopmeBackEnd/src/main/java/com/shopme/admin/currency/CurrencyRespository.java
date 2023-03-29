package com.shopme.admin.currency;

import com.shopme.entity.Currency;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRespository extends CrudRepository<Currency, Integer> {
}
