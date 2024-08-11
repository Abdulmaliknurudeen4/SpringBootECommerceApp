package com.shopme.settings;

import com.shopme.entity.setting.Setting;
import com.shopme.entity.setting.SettingsBag;

import java.util.List;

public class PaymentSettingBag extends SettingsBag {
    public PaymentSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getURL(){
        return super.getValue("PAYPAL_API_BASE_URL");
    }

    public String getClientID(){
        return super.getValue("PAYPAL_API_CLIENT_ID");
    }

    public String getClientSecret(){
        return super.getValue("PAYPAL_API_CLIENT_SECRET");
    }
}
