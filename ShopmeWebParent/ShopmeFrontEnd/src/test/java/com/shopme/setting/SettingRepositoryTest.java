package com.shopme.setting;

import com.shopme.entity.Setting;
import com.shopme.entity.SettingCategory;
import com.shopme.settings.SettingCategoryRepository;
import com.shopme.settings.SettingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {
    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private SettingCategoryRepository sgRepo;

    @Test
    public void testFindbySettingCategoreis() {
        SettingCategory GENERAL = sgRepo.findbyCategory("GENERAL");
        SettingCategory CURRENCY = sgRepo.findbyCategory("CURRENCY");

        List<Setting> settings = settingRepository
                .findByCategories(List.of(GENERAL, CURRENCY));
        settings.forEach(System.out::println);
        assertThat(settings).isNotNull();
    }

}
