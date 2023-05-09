package com.shopme.admin.customer.export;

import com.shopme.admin.AbstractExporter;
import com.shopme.entity.Customer;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CustomerCSVExporter extends AbstractExporter {

    public void export(List<Customer> customerList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "customer");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "City", "State", "Country"};
        String[] fieldMapping = {"id", "email", "firstName", "lastName", "city", "state", "country"};
        csvWriter.writeHeader(csvHeader);

        for (Customer customer : customerList) {
            csvWriter.write(customer, fieldMapping);
        }

        csvWriter.close();
    }
}
