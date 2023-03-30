package com.shopme.admin.setting;

import com.shopme.entity.Setting;
import com.shopme.entity.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {

    @Autowired
    SettingCategoryRepository settingCategoryRepository;
    @Autowired
    private SettingRepository settingRepository;

    @Test
    public void testCreateSettingCategory() {
        List<SettingCategory> settingCategoryList = new ArrayList<>();
        settingCategoryList.add(new SettingCategory("GENERAL"));
        settingCategoryList.add(new SettingCategory("MAIL_SERVER"));
        settingCategoryList.add(new SettingCategory("MAIL_TEMPLATE"));
        settingCategoryList.add(new SettingCategory("CURRENCY"));
        settingCategoryList.add(new SettingCategory("PAYMENT"));

        settingCategoryRepository.saveAll(settingCategoryList);
    }

    @Test
    public void testCreateGeneralSettings() {
        SettingCategory general = settingCategoryRepository.findbyCategory("GENERAL");
        Setting sitelogo = new Setting("SITE_LOGO", "Shopme.png", general);
        Setting copyright = new Setting("COPYRIGHT", "Copyright (c) 2023 Shopme Ltd.", general);
        Iterable<Setting> settings = settingRepository.saveAll(List.of(sitelogo, copyright));
        assertThat(settings).isNotEmpty();
    }

    @Test
    public void testCurrentSettings() {
        SettingCategory currency = settingCategoryRepository.findbyCategory("CURRENCY");

        Setting currencyId = new Setting("CURRENCY_ID", "1", currency);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", currency);
        Setting position = new Setting("CURRENCY_SYMBOL_POSITION", "before", currency);
        Setting pointype = new Setting("CURRENCY_POINT_TYPE", "POINT", currency);
        Setting digits = new Setting("DECIMAL_DIGITS", "2", currency);
        Setting thousandpointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", currency);

        Iterable<Setting> settings = settingRepository.saveAll(List.of(currencyId, symbol, position, pointype, digits, thousandpointType));
        assertThat(settings).isNotEmpty();
    }

    @Test
    public void testListSetting() {
        SettingCategory currency = settingCategoryRepository.findbyCategory("CURRENCY");
        List<Setting> settings = settingRepository.findBySettingCategory(currency);
        settings.forEach(System.out::println);
        assertThat(settings).isNotEmpty();
    }
}
