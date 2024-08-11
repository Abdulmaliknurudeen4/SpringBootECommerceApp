package com.shopme.settings;

import com.shopme.entity.setting.Setting;
import com.shopme.entity.setting.SettingsBag;

import java.util.List;

public class CurrencySettingBag extends SettingsBag {
    public CurrencySettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getSymbol(){
        return super.getValue("CURRENCY_SYMBOL");
    }

    public String getSymbolPosition(){
        return super.getValue("CURRENCY_SYMBOL_POSITION");
    }

    public String getDecimalPointType(){
        return super.getValue("CURRENCY_POINT_TYPE");
    }

    public String getThousandsPointType(){
        return super.getValue("THOUSANDS_POINT_TYPE");
    }

    public Integer getDecimalDigits(){
        return Integer.parseInt(super.getValue("DECIMAL_DIGITS"));
    }

}
