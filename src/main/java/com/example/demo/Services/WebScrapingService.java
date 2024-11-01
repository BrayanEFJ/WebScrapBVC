package com.example.demo.Services;

import com.example.demo.Models.Dtostockdata;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
// ... otras importaciones ...

@Service
public class WebScrapingService {

    public List<Dtostockdata> scrapeDtostockdata(String parametro, LocalDate fecha) {
        String URL = "https://www.bvc.com.co/mercado-local-en-linea?tab=renta-variable_mercado-" + parametro;

        List<Dtostockdata> dataList = new ArrayList<>();
        Set<String> seenNemotecnicos = new HashSet<>();

        try {
            // Configurar ChromeDriver
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // Puedes quitar este comentario para el modo headless
            options.addArguments("--window-size=1920,1080");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            WebDriver driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            try {
                // Navegar a la URL
                driver.get(URL);

                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                try {
                    FileUtils.copyFile(screenshot, new File("screenshot.png"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Esperar a que los campos de fecha estén presentes
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".react-date-picker__inputGroup")));

                // Establecer los valores de fecha
                setDateFields(driver, fecha);

                // Esperar a que la tabla se actualice después de cambiar la fecha
                Thread.sleep(2000); // Considera reemplazar esto con una espera explícita más confiable

                // Esperar a que la tabla esté visible y cargue el CSS
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("table.Tablestyled__StyledTable-sc-1ie6ajo-2")));

                // Obtener el HTML actualizado
                String html = driver.getPageSource();
                Document doc = Jsoup.parse(html);

                // Encontrar la tabla
                Element table = doc.select("table.Tablestyled__StyledTable-sc-1ie6ajo-2").first();
                if (table != null) {
                    Elements rows = table.select("tbody tr");

                    for (Element row : rows) {
                        Elements cells = row.select("td");
                        if (!cells.isEmpty()) {
                            String nemotecnico = cells.get(0).select("p").text().trim();

                            if (seenNemotecnicos.contains(nemotecnico)) {
                                continue;
                            }
                            seenNemotecnicos.add(nemotecnico);

                            Dtostockdata stockData = new Dtostockdata();
                            stockData.setNemotecnico(nemotecnico);
                            stockData.setUltimoPrecio(cells.get(1).select("p").text().trim());
                            stockData.setVariacionPorcentual(cells.get(2).select("p").text().trim());
                            stockData.setVolumenes(cells.get(3).select("p").text().trim());
                            stockData.setCantidad(cells.get(4).select("p").text().trim());
                            stockData.setVariacionAbsoluta(cells.get(5).select("p").text().trim());
                            stockData.setPrecioApertura(cells.get(6).select("p").text().trim());
                            stockData.setPrecioMaximo(cells.get(7).select("p").text().trim());
                            stockData.setPrecioMinimo(cells.get(8).select("p").text().trim());
                            stockData.setPrecioPromedio(cells.get(9).select("p").text().trim());
                            stockData.setEmisorNombre(cells.get(10).select("p").text().trim());

                            dataList.add(stockData);
                        }
                    }
                }
            } finally {
                driver.quit();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al realizar web scraping: " + e.getMessage(), e);
        }

        return dataList;
    }

    private void setDateFields(WebDriver driver, LocalDate fecha) {
        try {
            // Encontrar los campos de fecha
            WebElement yearInput = driver.findElement(
                    By.cssSelector("input.react-date-picker__inputGroup__year"));
            WebElement monthInput = driver.findElement(
                    By.cssSelector("input.react-date-picker__inputGroup__month"));
            WebElement dayInput = driver.findElement(
                    By.cssSelector("input.react-date-picker__inputGroup__day"));

            // Limpiar y establecer el año
            yearInput.clear();
            yearInput.sendKeys(String.valueOf(fecha.getYear()));

            // Limpiar y establecer el mes
            monthInput.clear();
            monthInput.sendKeys(String.valueOf(fecha.getMonthValue()));

            // Limpiar y establecer el día
            dayInput.clear();
            dayInput.sendKeys(String.valueOf(fecha.getDayOfMonth()));

            // Dar tiempo para que se procese el cambio de fecha
            Thread.sleep(1000); // Considera reemplazar esto con una espera explícita más confiable

        } catch (Exception e) {
            throw new RuntimeException("Error al establecer la fecha: " + e.getMessage(), e);
        }
    }
}
