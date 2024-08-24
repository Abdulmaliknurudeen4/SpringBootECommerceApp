package com.shopme.admin.report;

import com.shopme.admin.setting.SettingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    @Autowired
    private SettingService settingService;

    @GetMapping("/reports")
    public String viewSalesReportHome(HttpServletRequest request){
        loadCurrencySetting(request);
        return "reports/reports";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        settingService.getCurrencySettings()
                .forEach(c -> {
                    request.setAttribute(c.getSettingKey(), c.getValue());
                });
    }
}
