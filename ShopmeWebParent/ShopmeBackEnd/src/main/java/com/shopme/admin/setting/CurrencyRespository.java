package com.shopme.admin.setting;

import com.shopme.entity.Currency;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CurrencyRespository extends CrudRepository<Currency, Integer> {
    public List<Currency> findAllByOrderByNameAsc();
}
