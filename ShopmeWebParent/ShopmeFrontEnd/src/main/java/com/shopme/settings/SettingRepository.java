package com.shopme.settings;

import com.shopme.entity.setting.Setting;
import com.shopme.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting, String> {

    public List<Setting> findBySettingCategory(SettingCategory category);

    @Query("SELECT s FROM Setting s WHERE s.settingCategory IN(?1)")
    public List<Setting> findByCategories(List<SettingCategory> categories);
}