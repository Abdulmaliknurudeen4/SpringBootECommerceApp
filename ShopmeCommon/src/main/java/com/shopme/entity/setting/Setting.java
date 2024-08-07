package com.shopme.entity.setting;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "settings")
public class Setting {
    @Id
    @Column(nullable = false, length = 128)
    private String settingKey;
    @Column(nullable = false, length = 1024)
    private String value;
    @OneToOne
    private SettingCategory settingCategory;

    public Setting() {
    }

    public Setting(String settingKey) {
        this.settingKey = settingKey;
    }

    public Setting(String settingKey, String value, SettingCategory settingCategory) {
        this.settingKey = settingKey;
        this.value = value;
        this.settingCategory = settingCategory;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SettingCategory getSettingCategory() {
        return settingCategory;
    }

    public void setSettingCategory(SettingCategory settingCategory) {
        this.settingCategory = settingCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setting setting = (Setting) o;
        return settingKey.equals(setting.settingKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(settingKey);

    }

    @Override
    public String toString() {
        return "Setting [ key =" + settingKey + ", value = " + value + " ]";
    }
}
