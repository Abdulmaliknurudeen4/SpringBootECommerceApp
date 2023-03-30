package com.shopme.admin.setting;

import com.shopme.entity.Setting;
import com.shopme.entity.SettingCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting, String> {

    public List<Setting> findBySettingCategory(SettingCategory category);
}