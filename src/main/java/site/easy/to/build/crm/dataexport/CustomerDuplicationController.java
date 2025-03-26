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
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.service.budget.CustomerBudgetService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;

@Controller
@RequiredArgsConstructor
public class CustomerDuplicationController {
    private final CustomerServiceImpl customerService;
    private final CustomerBudgetService customerBudgetService;
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;

    @GetMapping("/customers/duplicate")
    public void downloadCustomerData(
            @RequestParam("customerId") Integer customerId,
            HttpServletResponse response) throws IOException {

        Customer customer = customerService.findByCustomerId(customerId);
        List<CustomerBudgetDto> customerBudgets = customerBudgetService.getBudgetsByCustomerId(customerId);
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
        } catch (IOException e) {
            throw new IOException("Error writing CSV to response", e);
        }
    }
}