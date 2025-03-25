package site.easy.to.build.crm.service.csv;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Data;
import site.easy.to.build.crm.entity.CustomerBudget;
import site.easy.to.build.crm.exception.InvalidCsvFormatException;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;

@Service
@Data
public class BudgetCsvImportService {
    private String filename;
    private Iterable<CSVRecord> budgetRecords;
    private List<CustomerBudget> customerBudgets = new ArrayList<>();
    private List<InvalidCsvFormatException> exceptions = new ArrayList<>();

    private CustomerCsvImportService customerCsvImportService;
    private final CustomerServiceImpl customerService;

    /* ------------------------------ Constructors ------------------------------ */

    @Autowired
    public BudgetCsvImportService(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    public BudgetCsvImportService(String filename, Iterable<CSVRecord> budgetRecords,
            CustomerCsvImportService customerCsvImportService, CustomerServiceImpl customerService) {
        this.filename = filename;
        this.budgetRecords = budgetRecords;
        this.customerCsvImportService = customerCsvImportService;
        this.customerService = customerService;
    }

    /* --------------------------- Processing methods --------------------------- */

    public void processBudgetCsv() {
        int lineNumber = 1;
        for (CSVRecord budgetRecord : budgetRecords) {
            if (lineNumber > 1) { // Skip header
                customerBudgets.add(parseToCustomerBudget(budgetRecord, lineNumber));
            }
            lineNumber++;
        }
    }

    /* --------------------------- Validation methods --------------------------- */

    public boolean hasError() {
        return !exceptions.isEmpty();
    }

    /* ----------------------------- Parsing methods ---------------------------- */

    private CustomerBudget parseToCustomerBudget(CSVRecord budgetRecord, int lineNumber) {
        CustomerBudget customerBudget = new CustomerBudget();
        String email = budgetRecord.get(0);
        String strBudget = budgetRecord.get(1);

        if (!CsvValidationUtils.isValidEmail(email)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid email format provided"));
        }

        if (!customerCsvImportService.emailExists(email)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber,
                    "The customer with the provided email does not exist"));
        }

        double amount = CsvValidationUtils.parseAmount(strBudget, "0");
        if (!CsvValidationUtils.isValidAmount(strBudget)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid budget value provided"));
        }

        LocalDate today = LocalDate.now();
        customerBudget.setAmount(amount);
        customerBudget.setStartDate(Date.valueOf(today));
        customerBudget.setEndDate(Date.valueOf(today.plusYears(1)));

        return customerBudget;
    }
}