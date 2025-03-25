package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        if (customerCsv.isEmpty() || expenseCsv.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "One or more files are missing. Please select all files to upload");
            return "redirect:/csv-import";
        }

        return "redirect:/csv-import";
    }
}
