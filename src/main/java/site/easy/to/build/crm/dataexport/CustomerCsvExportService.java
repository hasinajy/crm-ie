package site.easy.to.build.crm.dataexport;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.service.budget.CustomerBudgetService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;

import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

/**
 * Service class responsible for generating CSV content for customer data
 * export.
 */
@Service
@RequiredArgsConstructor
public class CustomerCsvExportService {

    private final CustomerServiceImpl customerService;
    private final CustomerBudgetService customerBudgetService;
    private final LeadServiceImpl leadService;
    private final TicketServiceImpl ticketService;
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;

    private static final String CSV_SEPARATOR = "\n\n";
    private static final String NEW_LINE = "\n";

    /**
     * Generates a CSV file containing customer-related data and writes it to the
     * provided writer.
     *
     * @param customerId the ID of the customer whose data is to be exported
     * @param format     the desired output format ("original" or "enhanced")
     * @param writer     the PrintWriter to write the CSV content to
     * @throws IOException if an error occurs while writing the CSV content
     */
    public void generateCustomerCsv(Integer customerId, String format, PrintWriter writer) throws IOException {
        // Fetch all required data
        CustomerData data = fetchCustomerData(customerId);

        // Generate CSV based on format
        if ("original".equalsIgnoreCase(format)) {
            generateOriginalCsv(data, writer);
        } else {
            generateEnhancedCsv(data, writer);
        }
    }

    /**
     * Fetches all customer-related data needed for CSV generation.
     *
     * @param customerId the ID of the customer
     * @return a CustomerData object containing all required entities
     */
    private CustomerData fetchCustomerData(Integer customerId) {
        Customer customer = customerService.findByCustomerId(customerId);
        List<CustomerBudgetDto> customerBudgets = customerBudgetService.getBudgetsByCustomerId(customerId);
        List<Lead> leads = leadService.getCustomerLeads(customerId);
        List<Ticket> tickets = ticketService.findCustomerTickets(customerId);
        List<LeadExpense> leadExpenses = leadExpenseService.getAllLeadExpensesByCustomerId(customerId);
        List<TicketExpense> ticketExpenses = ticketExpenseService.getAllTicketExpensesByCustomerId(customerId);

        return new CustomerData(customer, customerBudgets, leads, tickets, leadExpenses, ticketExpenses);
    }

    /**
     * Generates the CSV in the original format.
     *
     * @param data   the customer data to include in the CSV
     * @param writer the PrintWriter to write the CSV content to
     * @throws IOException if an error occurs while writing
     */
    private void generateOriginalCsv(CustomerData data, PrintWriter writer) {
        String emailCopy = CustomerCsvExportUtil.getEmailCopy(data.customer.getEmail());

        // Customer section
        writer.write("customer_email,customer_name\n");
        writer.write(CustomerCsvExportUtil.getCustomerCsvOriginal(data.customer));
        writer.write(CSV_SEPARATOR);

        // Budget section
        writer.write("customer_email,Budget\n");
        for (CustomerBudgetDto budget : data.customerBudgets) {
            writer.write(CustomerCsvExportUtil.getCustomerBudgetCsv(emailCopy, budget));
            writer.write(NEW_LINE);
        }
        writer.write(CSV_SEPARATOR);

        // Expense section
        writer.write("customer_email,subject_or_name,type,status,expense\n");
        for (LeadExpense leadExpense : data.leadExpenses) {
            writer.write(CustomerCsvExportUtil.getLeadExpenseCsvOriginal(emailCopy, leadExpense));
            writer.write(NEW_LINE);
        }
        for (TicketExpense ticketExpense : data.ticketExpenses) {
            writer.write(CustomerCsvExportUtil.getTicketExpenseCsvOriginal(emailCopy, ticketExpense));
            writer.write(NEW_LINE);
        }
    }

    /**
     * Generates the CSV in the enhanced format.
     *
     * @param data   the customer data to include in the CSV
     * @param writer the PrintWriter to write the CSV content to
     * @throws IOException if an error occurs while writing
     */
    private void generateEnhancedCsv(CustomerData data, PrintWriter writer) {
        String emailCopy = CustomerCsvExportUtil.getEmailCopy(data.customer.getEmail());

        // Customer section
        writer.write(
                "CUSTOMER:name,email,position,phone,address,city,state,country,description,twitter,facebook,youtube,user_username\n");
        writer.write(CustomerCsvExportUtil.getCustomerCsv(data.customer));
        writer.write(CSV_SEPARATOR);

        // Budget section
        writer.write("BUDGET:customer_email,Budget\n");
        for (CustomerBudgetDto budget : data.customerBudgets) {
            writer.write(CustomerCsvExportUtil.getCustomerBudgetCsv(emailCopy, budget));
            writer.write(NEW_LINE);
        }
        writer.write(CSV_SEPARATOR);

        // Expense section
        writer.write("EXPENSE:type,lead_name_or_subject,description,amount\n");
        for (LeadExpense leadExpense : data.leadExpenses) {
            writer.write(CustomerCsvExportUtil.getLeadExpenseCsv(leadExpense));
            writer.write(NEW_LINE);
        }
        for (TicketExpense ticketExpense : data.ticketExpenses) {
            writer.write(CustomerCsvExportUtil.getTicketExpenseCsv(ticketExpense));
            writer.write(NEW_LINE);
        }
        writer.write(CSV_SEPARATOR);

        // Lead section
        writer.write("LEAD:name,status,phone,manager_name,employee_name,customer_email\n");
        for (Lead lead : data.leads) {
            writer.write(CustomerCsvExportUtil.getLeadCsv(lead, emailCopy));
            writer.write(NEW_LINE);
        }
        writer.write(CSV_SEPARATOR);

        // Ticket section
        writer.write("TICKET:subject,description,status,priority,manager_name,employee_name,customer_email\n");
        for (Ticket ticket : data.tickets) {
            writer.write(CustomerCsvExportUtil.getTicketCsv(ticket, emailCopy));
            writer.write(NEW_LINE);
        }
    }

    /**
     * Data holder class for customer-related entities.
     */
    private static class CustomerData {
        final Customer customer;
        final List<CustomerBudgetDto> customerBudgets;
        final List<Lead> leads;
        final List<Ticket> tickets;
        final List<LeadExpense> leadExpenses;
        final List<TicketExpense> ticketExpenses;

        CustomerData(Customer customer, List<CustomerBudgetDto> customerBudgets, List<Lead> leads,
                List<Ticket> tickets, List<LeadExpense> leadExpenses, List<TicketExpense> ticketExpenses) {
            this.customer = customer;
            this.customerBudgets = customerBudgets;
            this.leads = leads;
            this.tickets = tickets;
            this.leadExpenses = leadExpenses;
            this.ticketExpenses = ticketExpenses;
        }
    }
}