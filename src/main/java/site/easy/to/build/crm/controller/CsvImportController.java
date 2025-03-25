package site.easy.to.build.crm.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.exception.InvalidCsvFormatException;
import site.easy.to.build.crm.service.csv.CustomerCsvImportService;

@Controller
public class CsvImportController {

    @GetMapping("/csv-import")
    public String displayCsvImportForm() {
        return "csv-import/index";
    }

    @PostMapping("/csv-import")
    public String processCsv(
            @RequestParam("customerCsvFile") MultipartFile customerCsv,
            @RequestParam("expensesCsvFile") MultipartFile expenseCsv,
            RedirectAttributes redirectAttributes) {

        boolean hasError = false;
        List<InvalidCsvFormatException> exceptions = new ArrayList<>();
        String pageRedirect = "redirect:/csv-import";

        if (customerCsv.isEmpty() || expenseCsv.isEmpty()) {
            hasError = true;
            redirectAttributes.addFlashAttribute("error",
                    "One or more files are missing. Please select all files to upload");
        } else {
            try (
                    Reader customerReader = new BufferedReader(new InputStreamReader(customerCsv.getInputStream()));
                    Reader expenseReader = new BufferedReader(new InputStreamReader(expenseCsv.getInputStream()))) {

                Iterable<CSVRecord> customerRecords = CSVFormat.DEFAULT.parse(customerReader);
                Iterable<CSVRecord> expenseRecords = CSVFormat.DEFAULT.parse(expenseReader);

                CustomerCsvImportService customerCsvImportService = new CustomerCsvImportService(
                        customerCsv.getOriginalFilename(), customerRecords);
                customerCsvImportService.processCustomerCsv();

                if (customerCsvImportService.hasError()) {
                    hasError = true;
                    exceptions.addAll(customerCsvImportService.getExceptions());
                }

                // TODO: Process expense records
            } catch (IOException e) {
                hasError = true;
            }
        }

        if (!hasError) {
            redirectAttributes.addFlashAttribute("message", "CSV file uploaded and processed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("invalidFormatErrors", exceptions);
            redirectAttributes.addFlashAttribute("message", "Failed to process CSV files.");
        }

        return pageRedirect;
    }
}
