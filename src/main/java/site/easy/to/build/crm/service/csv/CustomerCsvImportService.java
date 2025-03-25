package site.easy.to.build.crm.service.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.exception.InvalidCsvFormatException;

@Service
@Data
public class CustomerCsvImportService {
    private String filename;
    private Iterable<CSVRecord> customerRecords;
    private List<Customer> customers;
    private List<InvalidCsvFormatException> exceptions;

    /* ------------------------------ Constructors ------------------------------ */

    @Autowired
    public CustomerCsvImportService() {
    }

    public CustomerCsvImportService(String filename, Iterable<CSVRecord> customerRecords) {
        this.setFilename(filename);
        this.setCustomerRecords(customerRecords);
        this.setCustomers(new ArrayList<>());
        this.setExceptions(new ArrayList<>());
    }

    /* ----------------------------- Service methods ---------------------------- */

    public void processCustomerCsv() {
        int counter = 0;
        for (CSVRecord customerRecord : this.getCustomerRecords()) {
            if (counter > 0) {
                Customer customer = parseCsvRecord(customerRecord, counter);
                this.getCustomers().add(customer);
            }
            counter++;
        }
    }

    public boolean hasError() {
        return !this.getExceptions().isEmpty();
    }

    private Customer parseCsvRecord(CSVRecord customerRecord, int lineNumber) {
        Customer customer = new Customer();

        String email = customerRecord.get(0);
        String name = customerRecord.get(1);

        if (!isValidEmail(email)) {
            this.getExceptions().add(
                    new InvalidCsvFormatException(this.getFilename(), lineNumber, "Invalid email format provided"));
        }

        customer.setEmail(email);
        customer.setName(name);

        return customer;
    }

    /* -------------------------------- Utilities ------------------------------- */

    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public List<String> getAllEmails() {
        if (this.getCustomers() == null) {
            return new ArrayList<>();
        }

        return this.getCustomers().stream()
                .map(Customer::getEmail)
                .collect(Collectors.toList());
    }

    public boolean emailExists(String email) {
        if (this.getCustomers() == null || email == null || email.isEmpty()) {
            return false;
        }
        return this.getCustomers().stream()
                .anyMatch(customer -> customer.getEmail().equalsIgnoreCase(email));
    }
}
