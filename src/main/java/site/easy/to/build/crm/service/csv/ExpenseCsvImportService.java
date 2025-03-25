package site.easy.to.build.crm.service.csv;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;
import site.easy.to.build.crm.entity.Lead;
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
    private List<Lead> leads;
    private List<LeadExpense> leadExpenses;
    private List<TicketExpense> ticketExpenses;
    private List<InvalidCsvFormatException> exceptions;

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

        this.setFilename(filename);
        this.setExpenseRecords(expenseRecords);
        this.setLeads(new ArrayList<>());
        this.setLeadExpenses(new ArrayList<>());
        this.setTicketExpenses(new ArrayList<>());
        this.setExceptions(new ArrayList<>());

        this.customerCsvImportService = customerCsvImportService;

        this.userService = userService;
        this.customerService = customerService;
        this.leadService = leadService;
    }

    public void processCustomerCsv() {
        int counter = 1;
        for (CSVRecord expenseRecord : this.getExpenseRecords()) {
            if (counter > 1) {
                if (isLeadExpense(expenseRecord)) {
                    LeadExpense leadExpense = parseToLeadExpense(expenseRecord, counter);
                    this.getLeadExpenses().add(leadExpense);
                } else {
                    TicketExpense ticketExpense = parseToTicketExpense(expenseRecord, counter);
                    this.getTicketExpenses().add(ticketExpense);
                }
            }
            counter++;
        }
    }

    public boolean hasError() {
        return !this.getExceptions().isEmpty();
    }

    /* -------------------------------- Utilities ------------------------------- */

    private LeadExpense parseToLeadExpense(CSVRecord expenseRecord, int lineNumber) {
        LeadExpense leadExpense = new LeadExpense();

        String email = expenseRecord.get(0);
        String label = expenseRecord.get(1);
        String status = expenseRecord.get(3);
        String strAmount = expenseRecord.get(4).replace(",", ".");

        if (!isValidEmail(email)) {
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber, "Invalid email format provided"));
        }

        if (!this.getCustomerCsvImportService().emailExists(email)) {
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber,
                            "The customer with the provided email does no exist"));
        }

        if (!isValidLeadStatus(status)) {
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber, "Invalid status value provided"));
        }

        if (!isValidExpenseAmount(strAmount)) {
            strAmount = "0";
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber, "Invalid amount value provided"));
        }

        leadExpense.setDescription("Expense for " + label);
        leadExpense.setAmount(Double.parseDouble(strAmount));
        leadExpense.setDate(LocalDate.now());

        return leadExpense;
    }

    private TicketExpense parseToTicketExpense(CSVRecord expenseRecord, int lineNumber) {
        TicketExpense ticketExpense = new TicketExpense();

        String email = expenseRecord.get(0);
        String label = expenseRecord.get(1);
        String status = expenseRecord.get(3);
        String strAmount = expenseRecord.get(4).replace(",", ".");

        if (!isValidEmail(email)) {
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber, "Invalid email format provided"));
        }

        if (!this.getCustomerCsvImportService().emailExists(email)) {
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber,
                            "The customer with the provided email does no exist"));
        }

        if (!isValidTicketStatus(status)) {
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber, "Invalid status value provided"));
        }

        if (!isValidExpenseAmount(strAmount)) {
            strAmount = "0";
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber, "Invalid amount value provided"));
        }

        ticketExpense.setDescription("Expense for " + label);
        ticketExpense.setAmount(Double.parseDouble(strAmount));
        ticketExpense.setDate(LocalDate.now());

        return ticketExpense;
    }

    private static boolean isValidLeadStatus(String status) {
        if (status == null || status.isEmpty()) {
            return false;
        }

        List<String> validLeadStatuses = Arrays.asList(
                "meeting-to-schedule",
                "scheduled",
                "archived",
                "success",
                "assign-to-sales");

        return validLeadStatuses.contains(status.toLowerCase());
    }

    private static boolean isValidTicketStatus(String status) {
        if (status == null || status.isEmpty()) {
            return false;
        }

        List<String> validTicketStatuses = Arrays.asList(
                "open",
                "assigned",
                "on-hold",
                "in-progress",
                "resolved",
                "closed",
                "reopened",
                "pending-customer-response",
                "escalated",
                "archived");

        return validTicketStatuses.contains(status.toLowerCase());
    }

    private static boolean isLeadExpense(CSVRecord expenseRecord) {
        return expenseRecord.get(2).equalsIgnoreCase("lead");
    }

    private static boolean isValidExpenseAmount(String amount) {
        if (amount == null || amount.isEmpty()) {
            return false;
        }

        try {
            Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
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
