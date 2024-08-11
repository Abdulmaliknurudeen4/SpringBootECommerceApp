package com.shopme.settings;

import com.shopme.entity.Currency;
import com.shopme.entity.setting.Setting;
import com.shopme.entity.setting.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private SettingCategoryRepository sgRepo;

    @Autowired
    private CurrencyRepository currencyRepository;

    public List<Setting> getGeneralSettings() {
        SettingCategory GENERAL = sgRepo.findbyCategory("GENERAL");
        SettingCategory CURRENCY = sgRepo.findbyCategory("CURRENCY");
        return settingRepository.findByCategories(List.of(GENERAL, CURRENCY));
    }

    public EmailSettingBag getEmailSettings() {
        SettingCategory MAIL_SERVER = sgRepo.findbyCategory("MAIL_SERVER");
        SettingCategory MAIL_TEMPLATE = sgRepo.findbyCategory("MAIL_TEMPLATE");

        List<Setting> settings = settingRepository.findByCategories(List.of(MAIL_SERVER, MAIL_TEMPLATE));

        return new EmailSettingBag(settings);
    }

    public CurrencySettingBag getCurrencySettings() {
        List<Setting> settings = settingRepository.findBySettingCategory(sgRepo.findbyCategory("CURRENCY"));
        return new CurrencySettingBag(settings);
    }

    public PaymentSettingBag getPaymentSettings() {
        List<Setting> settings = settingRepository.findBySettingCategory(sgRepo.findbyCategory("PAYMENT"));
        return new PaymentSettingBag(settings);
    }

    public String getCurrencyCode() {
        Setting setting = settingRepository.findBySettingKey("CURRENCY_ID");
        Integer currencyId = Integer.parseInt(setting.getValue());
        Currency currency = currencyRepository.findById(currencyId).get();

        return currency.getCode();
    }
}
