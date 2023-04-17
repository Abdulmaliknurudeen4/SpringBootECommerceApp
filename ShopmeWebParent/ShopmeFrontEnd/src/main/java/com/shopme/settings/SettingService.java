package com.shopme.settings;

import com.shopme.entity.Setting;
import com.shopme.entity.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private SettingCategoryRepository sgRepo;

    public List<Setting> getGeneralSettings() {
        SettingCategory GENERAL = sgRepo.findbyCategory("GENERAL");
        SettingCategory CURRENCY = sgRepo.findbyCategory("CURRENCY");

        List<Setting> settings = settingRepository.findByCategories(List.of(GENERAL, CURRENCY));

        return settings;
    }
}
