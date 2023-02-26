package com.shopme.admin.category;

import com.shopme.admin.AbstractExporter;
import com.shopme.entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CategoryCSVExporter extends AbstractExporter {
    public void export(List<Category> categories, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "category");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Category ID", "Category Name"};
        String[] fieldMapping = {"id", "alias"};
        csvWriter.writeHeader(csvHeader);

        for(Category category : categories){
            csvWriter.write(category, fieldMapping);
        }

        csvWriter.close();
    }

}
