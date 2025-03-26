package site.easy.to.build.crm.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.service.budget.CustomerBudgetService;
import site.easy.to.build.crm.service.csv.BudgetCsvImportService;
import site.easy.to.build.crm.service.csv.CustomerCsvImportService;
import site.easy.to.build.crm.service.csv.ExpenseCsvImportService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;
import site.easy.to.build.crm.service.user.UserServiceImpl;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CsvImportApiController {
    private final TransactionTemplate transactionTemplate;

    private final UserServiceImpl userService;
    private final CustomerServiceImpl customerService;
    private final LeadServiceImpl leadService;
    private final TicketServiceImpl ticketService;
    private final CustomerBudgetService customerBudgetService;
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;

    @PostMapping("/v1/import-csv")
    public ResponseEntity<String> processCsv(
            @RequestParam("customerCsvFile") MultipartFile customerCsv,
            @RequestParam("budgetCsvFile") MultipartFile budgetCsv,
            @RequestParam("expenseCsvFile") MultipartFile expenseCsv) {

        return transactionTemplate.execute(status -> {
            try {
                if (customerCsv.isEmpty() || budgetCsv.isEmpty() || expenseCsv.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error processing CSV files");
                }

                try (
                        Reader customerReader = new BufferedReader(
                                new InputStreamReader(customerCsv.getInputStream()));
                        Reader budgetReader = new BufferedReader(
                                new InputStreamReader(budgetCsv.getInputStream()));
                        Reader expenseReader = new BufferedReader(
                                new InputStreamReader(expenseCsv.getInputStream()))) {

                    Iterable<CSVRecord> customerRecords = CSVFormat.DEFAULT.parse(customerReader);
                    Iterable<CSVRecord> budgetRecords = CSVFormat.DEFAULT.parse(budgetReader);
                    Iterable<CSVRecord> expenseRecords = CSVFormat.DEFAULT.parse(expenseReader);

                    CustomerCsvImportService customerCsvImportService = new CustomerCsvImportService(
                            customerCsv.getOriginalFilename(),
                            customerRecords,
                            customerService);
                    customerCsvImportService.processCustomerCsv();
                    if (customerCsvImportService.hasError()) {
                        status.setRollbackOnly();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error processing CSV files");
                    }
                    customerCsvImportService.save();

                    BudgetCsvImportService budgetCsvImportService = new BudgetCsvImportService(
                            budgetCsv.getOriginalFilename(),
                            budgetRecords,
                            customerCsvImportService,
                            customerService,
                            customerBudgetService);
                    budgetCsvImportService.processBudgetCsv();
                    if (budgetCsvImportService.hasError()) {
                        status.setRollbackOnly();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error processing CSV files");
                    }
                    budgetCsvImportService.save();

                    ExpenseCsvImportService expenseCsvImportService = new ExpenseCsvImportService(
                            expenseCsv.getOriginalFilename(),
                            expenseRecords,
                            customerCsvImportService,
                            userService,
                            customerService,
                            leadService,
                            ticketService,
                            leadExpenseService,
                            ticketExpenseService);
                    expenseCsvImportService.processCustomerCsv();
                    if (expenseCsvImportService.hasError()) {
                        status.setRollbackOnly();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error processing CSV files");
                    }
                    expenseCsvImportService.save();

                    return ResponseEntity.ok("CSV files uploaded and processed successfully!");
                } catch (Exception e) {
                    status.setRollbackOnly();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error processing CSV files");
                }

            } catch (Exception e) {
                status.setRollbackOnly();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing CSV files");
            }
        });
    }
}
