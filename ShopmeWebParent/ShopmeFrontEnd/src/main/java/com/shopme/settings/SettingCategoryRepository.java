package com.shopme.settings;

import com.shopme.entity.SettingCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SettingCategoryRepository extends CrudRepository<SettingCategory, Integer> {
    @Query("SELECT cs FROM SettingCategory  cs WHERE cs.category = ?1")
    public SettingCategory findbyCategory(String settingCategory);
}
