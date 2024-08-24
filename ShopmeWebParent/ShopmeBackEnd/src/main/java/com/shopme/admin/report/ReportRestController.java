package com.shopme.admin.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class ReportRestController {

    @Autowired
    private MasterOrderReportService masterOrderReportService;

    @GetMapping("/reports/sales_by_date/{period}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable("period") String period) {
        System.out.println("Report period: " + period);
        return switch (period) {
            case "last_7_days" -> {
                yield masterOrderReportService.getReportDataLast7Days();
            }
            case "last_28_days" -> {
                yield masterOrderReportService.getReportDataLast28Days();
            }
            case "last_6_months" -> {
                yield masterOrderReportService.getReportDataLast6Months();
            }
            case "last_year" -> {
                yield masterOrderReportService.getReportDataLastYear();
            }
            default -> masterOrderReportService.getReportDataLast7Days();
        };
    }

    @GetMapping("/reports/sales_by_date/{startDate}/{endDate}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse(startDate);
        Date endTime = dateFormatter.parse(endDate);
        return masterOrderReportService.getReportDataByDateRange(startTime, endTime);
    }
}
