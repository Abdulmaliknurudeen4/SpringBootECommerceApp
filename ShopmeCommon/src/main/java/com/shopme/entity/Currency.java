package com.shopme.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "currencies")
@Table
public class Currency extends IdBasedEntity{

    @Column(nullable = false, length = 64)
    private String name;
    @Column(nullable = false, length = 3)
    private String symbol;
    @Column(nullable = false, length = 4)
    private String code;

    public Currency() {

    }

    public Currency(String name, String symbol, String code) {
        this.name = name;
        this.symbol = symbol;
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(id, currency.id) && name.equals(currency.name) && symbol.equals(currency.symbol) && code.equals(currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, symbol, code);
    }

    @Override
    public String toString() {
        return new StringBuffer().append(name).append("-").append(code).append("-").append(symbol).toString();
    }
}
