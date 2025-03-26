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
        Customer customer = customerService.findByCustomerId(customerId);
        List<CustomerBudgetDto> customerBudgets = customerBudgetService.getBudgetsByCustomerId(customerId);
        List<Lead> leads = leadService.getCustomerLeads(customerId);
        List<Ticket> tickets = ticketService.findCustomerTickets(customerId);
        List<LeadExpense> leadExpenses = leadExpenseService.getAllLeadExpensesByCustomerId(customerId);
        List<TicketExpense> ticketExpenses = ticketExpenseService.getAllTicketExpensesByCustomerId(customerId);

        final String CSV_SEPARATOR = "\n\n";
        final String NEW_LINE = "\n";

        if ("original".equalsIgnoreCase(format)) {
            // Original format headers
            final String CUSTOMER_HEADER = "customer_email,customer_name\n";
            final String BUDGET_HEADER = "customer_email,Budget\n";
            final String EXPENSE_HEADER = "customer_email,subject_or_name,type,status,expense\n";

            String emailCopy = CustomerCsvExportUtil.getEmailCopy(customer.getEmail());

            // Write customer data (original format)
            writer.write(CUSTOMER_HEADER);
            writer.write(CustomerCsvExportUtil.getCustomerCsvOriginal(customer));
            writer.write(CSV_SEPARATOR);

            // Write budget data (original format)
            writer.write(BUDGET_HEADER);
            for (CustomerBudgetDto budget : customerBudgets) {
                writer.write(CustomerCsvExportUtil.getCustomerBudgetCsv(emailCopy, budget));
                writer.write(NEW_LINE);
            }
            writer.write(CSV_SEPARATOR);

            // Write expense data (original format)
            writer.write(EXPENSE_HEADER);
            for (LeadExpense leadExpense : leadExpenses) {
                writer.write(CustomerCsvExportUtil.getLeadExpenseCsvOriginal(emailCopy, leadExpense));
                writer.write(NEW_LINE);
            }
            for (TicketExpense ticketExpense : ticketExpenses) {
                writer.write(CustomerCsvExportUtil.getTicketExpenseCsvOriginal(emailCopy, ticketExpense));
                writer.write(NEW_LINE);
            }
            // No leads or tickets in original format
        } else {
            // Enhanced format headers (default)
            final String CUSTOMER_HEADER = "CUSTOMER:name,email,position,phone,address,city,state,country,description,twitter,facebook,youtube,user_username\n";
            final String BUDGET_HEADER = "BUDGET:customer_email,Budget\n";
            final String EXPENSE_HEADER = "EXPENSE:type,lead_name_or_subject,description,amount\n";
            final String LEAD_HEADER = "LEAD:name,status,phone,manager_name,employee_name,customer_email\n";
            final String TICKET_HEADER = "TICKET:subject,description,status,priority,manager_name,employee_name,customer_email\n";

            String emailCopy = CustomerCsvExportUtil.getEmailCopy(customer.getEmail());

            // Write customer data (enhanced format)
            writer.write(CUSTOMER_HEADER);
            writer.write(CustomerCsvExportUtil.getCustomerCsv(customer));
            writer.write(CSV_SEPARATOR);

            // Write budget data (enhanced format)
            writer.write(BUDGET_HEADER);
            for (CustomerBudgetDto budget : customerBudgets) {
                writer.write(CustomerCsvExportUtil.getCustomerBudgetCsv(emailCopy, budget));
                writer.write(NEW_LINE);
            }
            writer.write(CSV_SEPARATOR);

            // Write expense data (enhanced format)
            writer.write(EXPENSE_HEADER);
            for (LeadExpense leadExpense : leadExpenses) {
                writer.write(CustomerCsvExportUtil.getLeadExpenseCsv(leadExpense));
                writer.write(NEW_LINE);
            }
            for (TicketExpense ticketExpense : ticketExpenses) {
                writer.write(CustomerCsvExportUtil.getTicketExpenseCsv(ticketExpense));
                writer.write(NEW_LINE);
            }
            writer.write(CSV_SEPARATOR);

            // Write lead data (enhanced format)
            writer.write(LEAD_HEADER);
            for (Lead lead : leads) {
                writer.write(CustomerCsvExportUtil.getLeadCsv(lead, emailCopy));
                writer.write(NEW_LINE);
            }
            writer.write(CSV_SEPARATOR);

            // Write ticket data (enhanced format)
            writer.write(TICKET_HEADER);
            for (Ticket ticket : tickets) {
                writer.write(CustomerCsvExportUtil.getTicketCsv(ticket, emailCopy));
                writer.write(NEW_LINE);
            }
        }
    }
}