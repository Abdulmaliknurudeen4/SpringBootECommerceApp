package com.shopme.admin.user.export;

import com.shopme.admin.user.AbstractExporter;
import com.shopme.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class UserCSVExporter extends AbstractExporter {

    public void export(List<User> userList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
        csvWriter.writeHeader(csvHeader);

        for(User user : userList){
            csvWriter.write(user, fieldMapping);
        }

        csvWriter.close();
    }

    public void exportPDF(List<User> listAll, HttpServletResponse response) {
    }

    public void exportExcel(List<User> listAll, HttpServletResponse response) {
    }
}
