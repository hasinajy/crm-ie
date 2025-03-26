package site.easy.to.build.crm.dataexport;

import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.TicketExpense;

/**
 * Utility class providing helper methods for formatting CSV rows for customer
 * data export.
 */
public class CustomerCsvExportUtil {

    private CustomerCsvExportUtil() {
    }

    /**
     * Formats a ticket expense as a CSV row for the enhanced format.
     *
     * @param ticketExpense the ticket expense entity
     * @return a CSV-formatted string for the ticket expense
     */
    public static String getTicketExpenseCsv(TicketExpense ticketExpense) {
        Ticket ticket = ticketExpense.getTicket();
        return String.format("%s,%s,%s,%s",
                escapeCsv("ticket"),
                escapeCsv(ticket.getSubject()),
                escapeCsv(ticketExpense.getDescription() != null ? ticketExpense.getDescription() : ""),
                escapeCsv(ticketExpense.getAmount().toString()));
    }

    /**
     * Formats a ticket expense as a CSV row for the original format.
     *
     * @param email         the customer email to include in the row
     * @param ticketExpense the ticket expense entity
     * @return a CSV-formatted string for the ticket expense
     */
    public static String getTicketExpenseCsvOriginal(String email, TicketExpense ticketExpense) {
        Ticket ticket = ticketExpense.getTicket();
        return String.format("%s,%s,%s,%s,%s",
                escapeCsv(email),
                escapeCsv(ticket.getSubject()),
                escapeCsv("ticket"),
                escapeCsv(ticket.getStatus()),
                escapeCsv(ticketExpense.getAmount().toString()));
    }

    /**
     * Formats a lead expense as a CSV row for the enhanced format.
     *
     * @param leadExpense the lead expense entity
     * @return a CSV-formatted string for the lead expense
     */
    public static String getLeadExpenseCsv(LeadExpense leadExpense) {
        Lead lead = leadExpense.getLead();
        return String.format("%s,%s,%s,%s",
                escapeCsv("lead"),
                escapeCsv(lead.getName()),
                escapeCsv(leadExpense.getDescription() != null ? leadExpense.getDescription() : ""),
                escapeCsv(leadExpense.getAmount().toString()));
    }

    /**
     * Formats a lead expense as a CSV row for the original format.
     *
     * @param email       the customer email to include in the row
     * @param leadExpense the lead expense entity
     * @return a CSV-formatted string for the lead expense
     */
    public static String getLeadExpenseCsvOriginal(String email, LeadExpense leadExpense) {
        Lead lead = leadExpense.getLead();
        return String.format("%s,%s,%s,%s,%s",
                escapeCsv(email),
                escapeCsv(lead.getName()),
                escapeCsv("lead"),
                escapeCsv(lead.getStatus()),
                escapeCsv(leadExpense.getAmount().toString()));
    }

    /**
     * Formats a customer budget as a CSV row.
     *
     * @param email             the customer email to include in the row
     * @param customerBudgetDto the customer budget DTO
     * @return a CSV-formatted string for the customer budget
     */
    public static String getCustomerBudgetCsv(String email, CustomerBudgetDto customerBudgetDto) {
        return email + "," + customerBudgetDto.getAmount();
    }

    /**
     * Formats a customer as a CSV row with all required fields for the enhanced
     * format.
     *
     * @param customer the customer entity
     * @return a CSV-formatted string for the customer
     */
    public static String getCustomerCsv(Customer customer) {
        String userUsername = customer.getUser() != null ? customer.getUser().getUsername() : "N/A";
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                escapeCsv(getNameCopy(customer.getName())),
                escapeCsv(getEmailCopy(customer.getEmail())),
                escapeCsv(customer.getPosition() != null ? customer.getPosition() : ""),
                escapeCsv(customer.getPhone() != null ? customer.getPhone() : ""),
                escapeCsv(customer.getAddress() != null ? customer.getAddress() : ""),
                escapeCsv(customer.getCity() != null ? customer.getCity() : ""),
                escapeCsv(customer.getState() != null ? customer.getState() : ""),
                escapeCsv(customer.getCountry() != null ? customer.getCountry() : ""),
                escapeCsv(customer.getDescription() != null ? customer.getDescription() : ""),
                escapeCsv(customer.getTwitter() != null ? customer.getTwitter() : ""),
                escapeCsv(customer.getFacebook() != null ? customer.getFacebook() : ""),
                escapeCsv(customer.getYoutube() != null ? customer.getYoutube() : ""),
                escapeCsv(userUsername));
    }

    /**
     * Formats a customer as a CSV row for the original format.
     *
     * @param customer the customer entity
     * @return a CSV-formatted string for the customer
     */
    public static String getCustomerCsvOriginal(Customer customer) {
        return String.format("%s,%s",
                escapeCsv(getEmailCopy(customer.getEmail())),
                escapeCsv(customer.getName()));
    }

    /**
     * Generates a modified email address for duplication purposes.
     *
     * @param email the original email address
     * @return the modified email address with "copy_" prefix
     */
    public static String getEmailCopy(String email) {
        return "copy_" + email;
    }

    /**
     * Generates a modified name for duplication purposes.
     *
     * @param name the original name
     * @return the modified name with " copy" suffix
     */
    public static String getNameCopy(String name) {
        return name + " copy";
    }

    /**
     * Formats a lead as a CSV row for the enhanced format.
     *
     * @param lead          the lead entity
     * @param customerEmail the customer email to include in the row
     * @return a CSV-formatted string for the lead
     */
    public static String getLeadCsv(Lead lead, String customerEmail) {
        String managerName = lead.getManager() != null ? lead.getManager().getUsername() : "N/A";
        String employeeName = lead.getEmployee() != null ? lead.getEmployee().getUsername() : "N/A";
        return String.format("%s,%s,%s,%s,%s,%s",
                escapeCsv(lead.getName()),
                escapeCsv(lead.getStatus()),
                escapeCsv(lead.getPhone() != null ? lead.getPhone() : ""),
                escapeCsv(managerName),
                escapeCsv(employeeName),
                escapeCsv(customerEmail));
    }

    /**
     * Formats a ticket as a CSV row for the enhanced format.
     *
     * @param ticket        the ticket entity
     * @param customerEmail the customer email to include in the row
     * @return a CSV-formatted string for the ticket
     */
    public static String getTicketCsv(Ticket ticket, String customerEmail) {
        String managerName = ticket.getManager() != null ? ticket.getManager().getUsername() : "N/A";
        String employeeName = ticket.getEmployee() != null ? ticket.getEmployee().getUsername() : "N/A";
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                escapeCsv(ticket.getSubject()),
                escapeCsv(ticket.getDescription() != null ? ticket.getDescription() : ""),
                escapeCsv(ticket.getStatus()),
                escapeCsv(ticket.getPriority()),
                escapeCsv(managerName),
                escapeCsv(employeeName),
                escapeCsv(customerEmail));
    }

    /**
     * Escapes a value for safe inclusion in a CSV file.
     *
     * @param value the value to escape
     * @return the escaped value, enclosed in quotes if it contains commas or quotes
     */
    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}