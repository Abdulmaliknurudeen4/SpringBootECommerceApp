package com.shopme.admin.setting;

import com.shopme.entity.setting.Setting;
import com.shopme.entity.setting.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private SettingCategoryRepository sgRepo;

    public List<Setting> listAllSettings() {
        return (List<Setting>) settingRepository.findAll();
    }

    public GeneralSettingBag getGeneralSettings() {
        SettingCategory GENERAL = sgRepo.findbyCategory("GENERAL");
        SettingCategory CURRENCY = sgRepo.findbyCategory("CURRENCY");

        List<Setting> settings = new ArrayList<>();

        List<Setting> generalSettings = settingRepository.findBySettingCategory(GENERAL);
        List<Setting> currencySettings = settingRepository.findBySettingCategory(CURRENCY);

        settings.addAll(generalSettings);
        settings.addAll(currencySettings);

        return new GeneralSettingBag(settings);
    }

    public void saveAll(Iterable<Setting> settingIterator) {
        settingRepository.saveAll(settingIterator);
    }

    public List<Setting> getMailServerSettings() {
        SettingCategory mailServer = sgRepo.findbyCategory("MAIL_SERVER");

        return settingRepository.findBySettingCategory(mailServer);
    }

    public List<Setting> getMailTemplateSettings() {
        SettingCategory mailTemplate = sgRepo.findbyCategory("MAIL_TEMPLATE");
        return settingRepository.findBySettingCategory(mailTemplate);
    }

    public List<Setting> getCurrencySettings() {
        SettingCategory CURRENCY = sgRepo.findbyCategory("CURRENCY");
        return settingRepository.findBySettingCategory(CURRENCY);
    }

    public List<Setting> getPaymentSettings() {
        SettingCategory PAYMENT = sgRepo.findbyCategory("PAYMENT");
        return settingRepository.findBySettingCategory(PAYMENT);
    }


}
