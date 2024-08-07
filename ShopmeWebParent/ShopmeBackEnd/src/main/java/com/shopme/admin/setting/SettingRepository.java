package com.shopme.admin.setting;

import com.shopme.entity.setting.Setting;
import com.shopme.entity.setting.SettingCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting, String> {

    public List<Setting> findBySettingCategory(SettingCategory category);
}