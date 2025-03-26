package site.easy.to.build.crm.dataexport;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

@Controller
@RequiredArgsConstructor
public class CustomerDuplicationController {
    private final CustomerServiceImpl customerService;
    private final CustomerBudgetService customerBudgetService;
    private final LeadServiceImpl leadService;
    private final TicketServiceImpl ticketService;
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;

    @GetMapping("/customers/download-csv")
    public void downloadCustomerData(
            @RequestParam("customerId") Integer customerId,
            HttpServletResponse response) throws IOException {

        Customer customer = customerService.findByCustomerId(customerId);
        List<CustomerBudgetDto> customerBudgets = customerBudgetService.getBudgetsByCustomerId(customerId);
        List<Lead> leads = leadService.getCustomerLeads(customerId);
        List<Ticket> tickets = ticketService.findCustomerTickets(customerId);
        List<LeadExpense> leadExpenses = leadExpenseService.getAllLeadExpensesByCustomerId(customerId);
        List<TicketExpense> ticketExpenses = ticketExpenseService.getAllTicketExpensesByCustomerId(customerId);

        // Set response headers
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"customer_data_" + customerId + ".csv\"");

        // Write CSV directly to response
        try (PrintWriter writer = response.getWriter()) {
            final String CSV_SEPARATOR = "\n\n";
            final String NEW_LINE = "\n";

            final String CUSTOMER_HEADER = "customer_email,customer_name\n";
            final String BUDGET_HEADER = "customer_email,Budget\n";
            final String EXPENSE_HEADER = "customer_email,subject_or_name,type,status,expense\n";
            final String LEAD_HEADER = "name,status,phone,manager_name,employee_name,customer_email\n";
            final String TICKET_HEADER = "subject,description,status,priority,manager_name,employee_name,customer_email\n";

            String emailCopy = CustomerDuplicationUtil.getEmailCopy(customer.getEmail());

            // Write all customer data
            writer.write(CUSTOMER_HEADER);
            writer.write(CustomerDuplicationUtil.getCustomerCsv(customer));
            writer.write(CSV_SEPARATOR);

            // Write all budget data
            writer.write(BUDGET_HEADER);
            for (CustomerBudgetDto budget : customerBudgets) {
                writer.write(CustomerDuplicationUtil.getCustomerBudgetCsv(emailCopy, budget));
                writer.write(NEW_LINE);
            }
            writer.write(CSV_SEPARATOR);

            // Write all expense data
            writer.write(EXPENSE_HEADER);
            for (LeadExpense leadExpense : leadExpenses) {
                writer.write(CustomerDuplicationUtil.getLeadExpenseCsv(emailCopy, leadExpense));
                writer.write(NEW_LINE);
            }
            for (TicketExpense ticketExpense : ticketExpenses) {
                writer.write(CustomerDuplicationUtil.getTicketExpenseCsv(emailCopy, ticketExpense));
                writer.write(NEW_LINE);
            }
            writer.write(CSV_SEPARATOR);

            // Write all lead data
            writer.write(LEAD_HEADER);
            for (Lead lead : leads) {
                writer.write(getLeadCsv(lead, emailCopy));
                writer.write(NEW_LINE);
            }
            writer.write(CSV_SEPARATOR);

            // Write all ticket data
            writer.write(TICKET_HEADER);
            for (Ticket ticket : tickets) {
                writer.write(getTicketCsv(ticket, emailCopy));
                writer.write(NEW_LINE);
            }
        } catch (IOException e) {
            throw new IOException("Error writing CSV to response", e);
        }
    }

    private String getLeadCsv(Lead lead, String customerEmail) {
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

    private String getTicketCsv(Ticket ticket, String customerEmail) {
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

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Escape commas and quotes in CSV values
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}