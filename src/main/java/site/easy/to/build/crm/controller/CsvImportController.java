package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CsvImportController {

    @GetMapping("/csv-import")
    public String displayCsvImportForm() {
        return "csv-import/index";
    }
}
