package com.example.demo.Services;

import com.example.demo.Models.Dtostockdata;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExcelExportService {

    // Escribir el archivo Excel a disco con try-with-resources
    public void exportToExcel(List<Dtostockdata> dataList, String filePath) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stock Data");

        // Crear el encabezado de la tabla
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Nemotécnico", "Último Precio", "Variación Porcentual", "Volúmenes", "Cantidad",
            "Variación Absoluta", "Precio Apertura", "Precio Máximo", "Precio Mínimo",
            "Precio Promedio", "Nombre Emisor"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Poblar la hoja de Excel con los datos
        int rowNum = 1;
        for (Dtostockdata stockData : dataList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(stockData.getNemotecnico());
            row.createCell(1).setCellValue(stockData.getUltimoPrecio());
            row.createCell(2).setCellValue(stockData.getVariacionPorcentual());
            row.createCell(3).setCellValue(stockData.getVolumenes());
            row.createCell(4).setCellValue(stockData.getCantidad());
            row.createCell(5).setCellValue(stockData.getVariacionAbsoluta());
            row.createCell(6).setCellValue(stockData.getPrecioApertura());
            row.createCell(7).setCellValue(stockData.getPrecioMaximo());
            row.createCell(8).setCellValue(stockData.getPrecioMinimo());
            row.createCell(9).setCellValue(stockData.getPrecioPromedio());
            row.createCell(10).setCellValue(stockData.getEmisorNombre());
        }

        // Escribir el archivo Excel a disco
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir archivo Excel: " + e.getMessage(), e);
        } finally {
            try {
                workbook.close(); // Cerrar el workbook también
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
