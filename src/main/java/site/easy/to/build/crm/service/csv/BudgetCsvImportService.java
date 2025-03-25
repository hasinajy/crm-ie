package site.easy.to.build.crm.service.csv;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private List<CustomerBudget> customerBudgets;
    private List<InvalidCsvFormatException> exceptions;

    private CustomerCsvImportService customerCsvImportService;

    private final CustomerServiceImpl customerService;

    /* ------------------------------ Constructors ------------------------------ */

    @Autowired
    public BudgetCsvImportService(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    public BudgetCsvImportService(
            String filename,
            Iterable<CSVRecord> budgetRecords,
            CustomerCsvImportService customerCsvImportService,
            CustomerServiceImpl customerService) {

        this.setFilename(filename);
        this.setBudgetRecords(budgetRecords);
        this.setCustomerBudgets(new ArrayList<>());
        this.setExceptions(new ArrayList<>());

        this.customerCsvImportService = customerCsvImportService;

        this.customerService = customerService;
    }

    public void processBudgetCsv() {
        int counter = 1;
        for (CSVRecord budgetRecord : this.getBudgetRecords()) {
            if (counter > 1) {
                CustomerBudget customerBudget = parseToCustomerBudget(budgetRecord, counter);
                this.getCustomerBudgets().add(customerBudget);
            }
            counter++;
        }
    }

    public boolean hasError() {
        return !this.getExceptions().isEmpty();
    }

    /* -------------------------------- Utilities ------------------------------- */

    private CustomerBudget parseToCustomerBudget(CSVRecord budgetRecord, int lineNumber) {
        CustomerBudget customerBudget = new CustomerBudget();

        String email = budgetRecord.get(0);
        String strBudget = budgetRecord.get(1).replace(",", ".");

        if (!isValidEmail(email)) {
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber, "Invalid email format provided"));
        }

        if (!this.getCustomerCsvImportService().emailExists(email)) {
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber,
                            "The customer with the provided email does not exist"));
        }

        if (!isValidBudgetAmount(strBudget)) {
            strBudget = "0";
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber, "Invalid budget value provided"));
        }

        LocalDate today = LocalDate.now();
        LocalDate yearFromToday = today.plusYears(1);

        customerBudget.setAmount(Double.parseDouble(strBudget));
        customerBudget.setStartDate(Date.valueOf(today));
        customerBudget.setEndDate(Date.valueOf(yearFromToday));

        return customerBudget;
    }

    private static boolean isValidBudgetAmount(String amount) {
        if (amount == null || amount.isEmpty()) {
            return false;
        }

        double numericAmount = 0;

        try {
            numericAmount = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            return false;
        }

        return numericAmount > 0;
    }

    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
