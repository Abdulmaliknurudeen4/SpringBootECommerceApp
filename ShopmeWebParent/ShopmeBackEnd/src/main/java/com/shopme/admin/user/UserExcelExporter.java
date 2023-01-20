package com.shopme.admin.user;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.shopme.entity.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UserExcelExporter extends AbstractExporter{

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public UserExcelExporter(){
        workbook = new XSSFWorkbook();
    }
    private void writeHeaderLine(){
        sheet = workbook.createSheet("Users");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "User ID", cellStyle);
        createCell(row, 1 ,"E-mail", cellStyle);
        createCell(row, 2,"First Name", cellStyle);
        createCell(row, 3,"Last Name", cellStyle);
        createCell(row, 4,"Roles", cellStyle);
        createCell(row, 5,"Enabled", cellStyle);

    }
    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style){
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);
        if(value instanceof Integer){
            cell.setCellValue((Integer)value);
        }else if (value instanceof Boolean){
            cell.setCellValue((Boolean) value);
        }else{
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    public void export(List<User> userList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");
        writeHeaderLine();
        writeDataLine(userList);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private void writeDataLine(List<User> listUsers) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(false);
        font.setFontHeight(12);
        cellStyle.setFont(font);
        int rowIndex = 1;
        for(User user: listUsers){
          XSSFRow row =  sheet.createRow(rowIndex++);
          int columnIndex = 0;
          createCell(row, columnIndex++, user.getId(), cellStyle);
          createCell(row, columnIndex++, user.getEmail(), cellStyle);
          createCell(row, columnIndex++, user.getFirstName(), cellStyle);
          createCell(row, columnIndex++, user.getLastName(), cellStyle);
          createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
          createCell(row, columnIndex, user.isEnabled(), cellStyle);
        }
    }
}