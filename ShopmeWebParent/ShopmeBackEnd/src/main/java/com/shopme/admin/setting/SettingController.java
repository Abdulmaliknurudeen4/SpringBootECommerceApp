package com.shopme.admin.setting;

import com.shopme.admin.FileUploadUtil;
import com.shopme.entity.Currency;
import com.shopme.entity.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class SettingController {

    @Autowired
    private SettingService service;
    @Autowired
    private CurrencyRespository currencyRespository;

    private static void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            settingBag.updateSiteLogo(value);

            String uploadDir = "../site-logo/";
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    @GetMapping("/settings")
    public String listAll(Model model) {
        List<Setting> settings = service.listAllSettings();
        List<Currency> listCurrencies = currencyRespository.findAllByOrderByNameAsc();

        settings.forEach(s -> model.addAttribute(s.getSettingKey(), s.getValue()));
        model.addAttribute("listCurrencies", listCurrencies);

        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam("SITE_LOGO") MultipartFile multipartFile,
                                      HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        GeneralSettingBag settingBag = service.getGeneralSettings();
        saveSiteLogo(multipartFile, settingBag);
        saveCurrencySymbol(request, settingBag);
        updateSettingValueFromForm(request, settingBag.list());
        redirectAttributes.addFlashAttribute("message", "General Settings have been saved");
        return "redirect:/settings";
    }

    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> byId = currencyRespository.findById(currencyId);
        if (byId.isPresent()) {
            Currency currency = byId.get();
            settingBag.updateCurrencySymbol(currency.getSymbol());
        }

    }

    private void updateSettingValueFromForm(HttpServletRequest request, List<Setting> listSettings) {
        for (Setting setting : listSettings) {
            String value = request.getParameter(setting.getSettingKey());
            if (value != null) {
                setting.setValue(value);
            }
        }

        service.saveAll(listSettings);
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        GeneralSettingBag settingBag = service.getMailServerSettings();
        updateSettingValueFromForm(request, settingBag.list());
        redirectAttributes.addFlashAttribute("message", "Mail Server Settings have been saved");
        return "redirect:/settings";
    }

    @PostMapping("/settings/save_mail_template")
    public String saveMailTemplateSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        GeneralSettingBag settingBag = service.getMailTemplateSettings();
        updateSettingValueFromForm(request, settingBag.list());
        redirectAttributes.addFlashAttribute("message", "Mail Template Settings have been saved");
        return "redirect:/settings";
    }
}

