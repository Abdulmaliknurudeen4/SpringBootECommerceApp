package com.shopme.admin.setting;

import com.shopme.entity.Setting;
import com.shopme.entity.SettingsBag;

import java.util.List;

public class GeneralSettingBag extends SettingsBag {
    public GeneralSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public void updateCurrencySymbol(String value) {
        super.update("CURRENCY_SYMBOL", value);
    }

    public void updateSiteLogo(String value) {
        super.update("SITE_LOGO", value);
    }
}
