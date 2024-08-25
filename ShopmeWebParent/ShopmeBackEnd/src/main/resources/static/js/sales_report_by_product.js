//Sales Report by Product
function prepareChartDataForSalesByProduct(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Product');
    data.addColumn('number', 'Quantity');
    data.addColumn('number', 'Gross Sales');
    data.addColumn('number', 'Net Sales');

    let totalGrossSales = 0.0;
    let totalNetSales = 0.0;
    totalItems = 0.0;

    $.each(responseJSON, function (index, reportItem) {
        data.addRows([[reportItem.identifier, reportItem.productsCount,
            reportItem.grossSales, reportItem.netSales]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseFloat(reportItem.productsCount);
    });

}

function customizeChartForSalesByProduct() {
    chartOptions = {
        height: 360,
        width: '80%',
        showRowNumber: true,
        page: 'enable',
        sortColumn: 2,
        sortAscending: false
    };

}

function drawChartForSalesByProduct() {

    let salesChart = new google.visualization.Table(document.getElementById('chart_sales_by_product'));
    salesChart.draw(data, chartOptions);


}

function loadSalesReportByDateForProduct(period) {
    let requestURL = "";
    if (period === "custom") {
        let startDate = $('#startDate_category').val();
        let endDate = $('#endDate_category').val();
        requestURL = contextPath + "reports/product/" + startDate + "/" + endDate;

    } else {
        requestURL = contextPath + "reports/product/" + period;

    }
    $.get(requestURL, function (responseJSON) {
        prepareChartDataForSalesByProduct(responseJSON);
        customizeChartForSalesByProduct();
        formatChartData(data, 2, 3);
        drawChartForSalesByProduct(period);
        setSalesAmount(period, '_product', "Total Products");
    });
}


$(document).ready(function () {
    setUpButtonEventHandlers("_product", loadSalesReportByDateForProduct)
});