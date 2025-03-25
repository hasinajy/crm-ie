package site.easy.to.build.crm.service.csv;

import java.util.ArrayList;
import java.util.List;
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
    private List<Customer> customers = new ArrayList<>();
    private List<InvalidCsvFormatException> exceptions = new ArrayList<>();

    /* ------------------------------ Constructors ------------------------------ */

    @Autowired
    public CustomerCsvImportService() {
    }

    public CustomerCsvImportService(String filename, Iterable<CSVRecord> customerRecords) {
        this.filename = filename;
        this.customerRecords = customerRecords;
    }

    /* --------------------------- Processing methods --------------------------- */

    public void processCustomerCsv() {
        int lineNumber = 0;
        for (CSVRecord customerRecord : customerRecords) {
            if (lineNumber > 0) { // Skip header
                customers.add(parseCsvRecord(customerRecord, lineNumber));
            }
            lineNumber++;
        }
    }

    /* --------------------------- Validation methods --------------------------- */

    public boolean hasError() {
        return !exceptions.isEmpty();
    }

    public boolean emailExists(String email) {
        return email != null && !email.isEmpty() &&
                customers.stream().anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
    }

    /* ----------------------------- Parsing methods ---------------------------- */

    private Customer parseCsvRecord(CSVRecord customerRecord, int lineNumber) {
        Customer customer = new Customer();
        String email = customerRecord.get(0);
        String name = customerRecord.get(1);

        if (!CsvValidationUtils.isValidEmail(email)) {
            exceptions.add(new InvalidCsvFormatException(filename, lineNumber, "Invalid email format provided"));
        }

        customer.setEmail(email);
        customer.setName(name);
        return customer;
    }

    /* ----------------------------- Utility methods ---------------------------- */

    public List<String> getAllEmails() {
        return customers.stream()
                .map(Customer::getEmail)
                .toList();
    }
}