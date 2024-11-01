/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.Controller;

import com.example.demo.Models.Dtostockdata;
import com.example.demo.Services.ExcelExportService;
import com.example.demo.Services.WebScrapingService;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author l
 */
@Controller
@RequestMapping("/api/stocks/generateexcel")
public class StockDataController {

    @Autowired
    WebScrapingService scrapingService;

    @Autowired
    ExcelExportService exportService;

    @GetMapping
    public ResponseEntity<List<Dtostockdata>> obtenerYExportarDatos(
            @RequestParam(value = "parametro", defaultValue = "local") String parametro, // valor por defecto "local"
            @RequestParam(value = "fecha", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) { // fecha opcional

        if (fecha == null) {
            fecha = LocalDate.now();
        }

        List<Dtostockdata> stockDataList = scrapingService.scrapeDtostockdata(parametro, fecha);

        String filePath = "stock_data.xlsx";
        exportService.exportToExcel(stockDataList, filePath);
        try {
            String pathInterno = "C:\\Users\\l\\Documents\\Asignamiento_Worldskills\\WebScrap\\stock_data.xlsx";
            Runtime.getRuntime().exec("cmd /c start " + pathInterno);
        } catch (IOException e) {
            System.out.println("Error al intentar abrir el archivo Excel: " + e.getMessage());
        }
        return ResponseEntity.ok(stockDataList);
    }

}
