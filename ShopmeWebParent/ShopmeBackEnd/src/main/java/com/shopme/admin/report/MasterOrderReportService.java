package com.shopme.admin.report;

import com.shopme.admin.order.OrderRepository;
import com.shopme.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MasterOrderReportService extends AbstractReportService {

    @Autowired
    private OrderRepository repo;

    protected List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType) {
        List<Order> listOrders = repo.findByOrderTimeBetween(startTime, endTime);
        List<ReportItem> listReportItems = createReportData(startTime, endTime, reportType);

        calculateSalesForReportData(listOrders, listReportItems);

        return listReportItems;
    }

    private void calculateSalesForReportData(List<Order> listOrders, List<ReportItem> listReportItem) {
        for (Order order : listOrders) {
            String orderDateString = dateFormatter.format(order.getOrderTime());

            ReportItem reportItem = new ReportItem(orderDateString);
            int itemIndex = listReportItem.indexOf(reportItem);

            if (itemIndex >= 0) {
                reportItem = listReportItem.get(itemIndex);
                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
                reportItem.increaseOrdersCount();
            }
        }
    }


    private List<ReportItem> createReportData(Date startTime, Date endTime, ReportType reportType) {
        List<ReportItem> listReportItems = new ArrayList<>();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(startTime);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(endTime);

        Date currentDate = startDate.getTime();
        String dateString = dateFormatter.format(currentDate);

        listReportItems.add(new ReportItem(dateString));

        do {
            if (reportType.equals(ReportType.DAY)) {
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            } else if (reportType.equals(ReportType.MONTH)) {
                startDate.add(Calendar.MONTH, 1);
            }
            currentDate = startDate.getTime();
            dateString = dateFormatter.format(currentDate);
            listReportItems.add(new ReportItem(dateString));
        } while (startDate.before(endDate));

        return listReportItems;

    }


}
