//Sales Report by Category



function prepareChartDataForSalesByCategory(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Category Name');
    data.addColumn('number', 'Gross Sales');
    data.addColumn('number', 'Net Sales');

    let totalGrossSales = 0.0;
    let totalNetSales = 0.0;
    totalItems = 0.0;

    $.each(responseJSON, function (index, reportItem) {
        data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseFloat(reportItem.productsCount);
    });

}

function customizeChartForSalesByCategory() {
    chartOptions = {
        'height': 360, legend: {position: 'right'}
    };

}

function drawChartForSalesByCategory() {

    let salesChart = new google.visualization.PieChart(document.getElementById('chart_sales_by_category'));
    salesChart.draw(data, chartOptions);


}

function loadSalesReportByDateForCategory(period) {
    let requestURL = "";
    if (period === "custom") {
        let startDate = $('#startDate_category').val();
        let endDate = $('#endDate_category').val();
        requestURL = contextPath + "reports/category/" + startDate + "/" + endDate;

    } else {
        requestURL = contextPath + "reports/category/" + period;

    }
    $.get(requestURL, function (responseJSON) {
        prepareChartDataForSalesByCategory(responseJSON);
        customizeChartForSalesByCategory();
        formatChartData(data, 1, 2);
        drawChartForSalesByCategory(period);
        setSalesAmount(period, '_category', "Total Products");
    });
}


$(document).ready(function () {
    setUpButtonEventHandlers("_category", loadSalesReportByDateForCategory)
});