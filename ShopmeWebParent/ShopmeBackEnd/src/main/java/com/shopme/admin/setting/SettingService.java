package com.shopme.admin.setting;

import com.shopme.entity.Setting;
import com.shopme.entity.SettingCategory;
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

}
