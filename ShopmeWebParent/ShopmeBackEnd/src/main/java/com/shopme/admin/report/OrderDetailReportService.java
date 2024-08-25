package com.shopme.admin.report;

import com.shopme.admin.order.OrderDetailRepository;
import com.shopme.entity.order.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderDetailReportService extends AbstractReportService {

    @Autowired
    private OrderDetailRepository repo;

    @Override
    protected List<ReportItem> getReportDataByDateRangeInternal(Date startDate, Date endDate, ReportType reportType) {
        List<OrderDetail> listOrderDetail = null;
        if (reportType.equals(ReportType.CATEGORY)) {
            listOrderDetail = repo.findWithCategoryAndTimeBetween(startDate, endDate);
        } else if (reportType.equals(ReportType.PRODUCT)) {
            listOrderDetail = repo.findWithProductAndTimeBetween(startDate, endDate);
        }
        List<ReportItem> listReportItem = new ArrayList<>();
        for (OrderDetail detail : listOrderDetail) {
            String identifier = "";
            if (reportType.equals(ReportType.CATEGORY)) {
                identifier = detail.getProduct().getCategory().getName();
            } else if (reportType.equals(ReportType.PRODUCT)) {
                identifier = detail.getProduct().getShortName();
            }
            ReportItem reportItem = new ReportItem(identifier);
            int itemIndex = listReportItem.indexOf(reportItem);

            float grossSales = detail.getSubtotal() + detail.getShippingCost();
            float netSales = detail.getSubtotal() - detail.getProductCost();

            if (itemIndex >= 0) {
                reportItem = listReportItem.get(itemIndex);
                reportItem.addGrossSales(grossSales);
                reportItem.addNetSales(netSales);
                reportItem.increaseProductCount(detail.getQuality());

            } else {
                listReportItem.add(new ReportItem(identifier, grossSales, netSales, detail.getQuality()));
            }

        }
        return listReportItem;
    }


}
