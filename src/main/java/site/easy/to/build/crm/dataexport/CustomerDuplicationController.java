package site.easy.to.build.crm.dataexport;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Controller responsible for handling customer data export requests.
 */
@Controller
@RequiredArgsConstructor
public class CustomerDuplicationController {

    private final CustomerCsvExportService csvExportService;

    /**
     * Handles the request to download customer data as a CSV file.
     *
     * @param customerId the ID of the customer whose data is to be exported
     * @param response   the HTTP response to write the CSV file to
     * @throws IOException if an error occurs while writing to the response
     */
    @GetMapping("/customers/download-csv")
    public void downloadCustomerData(
            @RequestParam("customerId") Integer customerId,
            HttpServletResponse response) throws IOException {

        // Set response headers
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"customer_data_" + customerId + ".csv\"");

        // Delegate CSV generation to the service
        csvExportService.generateCustomerCsv(customerId, response.getWriter());
    }
}