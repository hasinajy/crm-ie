package site.easy.to.build.crm.service.csv;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Data;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.exception.InvalidCsvFormatException;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.user.UserServiceImpl;

@Service
@Data
public class ExpenseCsvImportService {
    private String filename;
    private Iterable<CSVRecord> expenseRecords;
    private List<LeadExpense> leadExpenses = new ArrayList<>();
    private List<TicketExpense> ticketExpenses = new ArrayList<>();
    private List<InvalidCsvFormatException> exceptions = new ArrayList<>();

    private CustomerCsvImportService customerCsvImportService;

    private final UserServiceImpl userService;
    private final CustomerServiceImpl customerService;
    private final LeadServiceImpl leadService;

    /* ------------------------------ Constructors ------------------------------ */

    @Autowired
    public ExpenseCsvImportService(
            UserServiceImpl userService,
            CustomerServiceImpl customerService,
            LeadServiceImpl leadService) {

        this.userService = userService;
        this.customerService = customerService;
        this.leadService = leadService;
    }

    public ExpenseCsvImportService(
            String filename,
            Iterable<CSVRecord> expenseRecords,
            CustomerCsvImportService customerCsvImportService,
            UserServiceImpl userService,
            CustomerServiceImpl customerService,
            LeadServiceImpl leadService) {

        this.filename = filename;
        this.expenseRecords = expenseRecords;
        this.customerCsvImportService = customerCsvImportService;
        this.userService = userService;
        this.customerService = customerService;
        this.leadService = leadService;
    }

    /* --------------------------- Processing methods --------------------------- */

    public void processCustomerCsv() {
        int lineNumber = 1;
        for (CSVRecord expenseRecord : expenseRecords) {
            if (lineNumber > 1) {
                if (isLeadExpense(expenseRecord)) {
                    leadExpenses.add(parseToLeadExpense(expenseRecord, lineNumber));
                } else {
                    ticketExpenses.add(parseToTicketExpense(expenseRecord, lineNumber));
                }
            }
            lineNumber++;
        }
    }

    /* --------------------------- Validation methods --------------------------- */

    public boolean hasError() {
        return !exceptions.isEmpty();
    }

    private boolean isValidLeadStatus(String status) {
        List<String> validStatuses = Arrays.asList(
                "meeting-to-schedule",
                "scheduled",
                "archived",
                "success",
                "assign-to-sales");
        return status != null && !status.isEmpty() && validStatuses.contains(status.toLowerCase());
    }

    private boolean isValidTicketStatus(String status) {
        List<String> validStatuses = Arrays.asList("open",
                "assigned",
                "on-hold",
                "in-progress",
                "resolved",
                "closed",
                "reopened",
                "pending-customer-response",
                "escalated",
                "archived");
        return status != null && !status.isEmpty() && validStatuses.contains(status.toLowerCase());
    }

    /* ----------------------------- Parsing methods ---------------------------- */

    private LeadExpense parseToLeadExpense(CSVRecord expenseRecord, int lineNumber) {
        LeadExpense leadExpense = new LeadExpense();
        validateCommonFields(expenseRecord, lineNumber);

        String status = expenseRecord.get(3);
        if (!isValidLeadStatus(status)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid status value provided"));
        }

        String label = expenseRecord.get(1);
        double amount = CsvValidationUtils.parseAmount(expenseRecord.get(4), "0");
        populateExpense(leadExpense, label, amount);
        return leadExpense;
    }

    private TicketExpense parseToTicketExpense(CSVRecord expenseRecord, int lineNumber) {
        TicketExpense ticketExpense = new TicketExpense();
        validateCommonFields(expenseRecord, lineNumber);

        String status = expenseRecord.get(3);
        if (!isValidTicketStatus(status)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid status value provided"));
        }

        String label = expenseRecord.get(1);
        double amount = CsvValidationUtils.parseAmount(expenseRecord.get(4), "0");
        populateExpense(ticketExpense, label, amount);
        return ticketExpense;
    }

    /* ----------------------------- Helper methods ----------------------------- */

    private void validateCommonFields(CSVRecord expenseRecord, int lineNumber) {
        String email = expenseRecord.get(0);
        if (!CsvValidationUtils.isValidEmail(email)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid email format provided"));
        }

        if (!customerCsvImportService.emailExists(email)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber,
                    "The customer with the provided email does not exist"));
        }

        if (!CsvValidationUtils.isValidAmount(expenseRecord.get(4))) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid amount value provided"));
        }
    }

    private void populateExpense(Object expense, String label, double amount) {
        if (expense instanceof LeadExpense lead) {
            lead.setDescription("Expense for " + label);
            lead.setAmount(amount);
            lead.setDate(LocalDate.now());
        } else if (expense instanceof TicketExpense ticket) {
            ticket.setDescription("Expense for " + label);
            ticket.setAmount(amount);
            ticket.setDate(LocalDate.now());
        }
    }

    private boolean isLeadExpense(CSVRecord expenseRecord) {
        return expenseRecord.get(2).equalsIgnoreCase("lead");
    }
}