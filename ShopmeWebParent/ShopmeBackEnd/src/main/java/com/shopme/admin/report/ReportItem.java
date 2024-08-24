package com.shopme.admin.report;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Setter
@Getter
@ToString
public class ReportItem {
    private String identifier;
    private float grossSales;
    private float netSales;
    private float ordersCount;

    public ReportItem(String identifier, float grossSales, float netSales) {
        this.identifier = identifier;
        this.grossSales = grossSales;
        this.netSales = netSales;
    }

    public ReportItem() {
    }

    public ReportItem(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportItem that = (ReportItem) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
    public void addGrossSales(float amount){
        this.grossSales += amount;
    }
    public void addNetSales(float amount){
        this.netSales += amount;
    }

    public void increateOrdersCount() {
        this.ordersCount++;
    }
}
