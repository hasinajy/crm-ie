package site.easy.to.build.crm.dataexport;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

@Controller
@RequiredArgsConstructor
public class CustomerDuplicationController {
    private final CustomerServiceImpl customerService;
    private final CustomerBudgetService customerBudgetService;
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;

    @GetMapping("/customers/duplicate")
    public String getMethodName(@RequestParam("customerId") Integer customerId) {
        Customer customer = customerService.findByCustomerId(customerId);
        List<CustomerBudgetDto> customerBudgets = customerBudgetService.getBudgetsByCustomerId(customerId);
        List<LeadExpense> leadExpenses = leadExpenseService.getAllLeadExpensesByCustomerId(customerId);
        List<TicketExpense> ticketExpenses = ticketExpenseService.getAllTicketExpensesByCustomerId(customerId);

        writeCsv(customer, customerBudgets, leadExpenses, ticketExpenses);
        return "redirect:/employee/customer/manager/all-customers";
    }

    private void writeCsv(
            Customer customer,
            List<CustomerBudgetDto> customerBudgets,
            List<LeadExpense> leadExpenses,
            List<TicketExpense> ticketExpenses) {

        try (FileWriter writer = new FileWriter("C:\\Users\\Hasina\\Desktop\\export.csv")) {
            final String CSV_SEPARATOR = "\n\n";
            final String NEW_LINE = "\n";

            final String CUSTOMER_HEADER = "customer_email,customer_name\n";
            final String BUDGET_HEADER = "customer_email,Budget\n";
            final String EXPENSE_HEADER = "customer_email,subject_or_name,type,status,expense\n";

            String emailCopy = getEmailCopy(customer.getEmail());

            // Write all customer data
            writer.write(CUSTOMER_HEADER);
            writer.write(getCustomerCsv(customer));
            writer.write(CSV_SEPARATOR);

            // Write all budget data
            writer.write(BUDGET_HEADER);
            for (CustomerBudgetDto budget : customerBudgets) {
                writer.write(getCustomerBudgetCsv(emailCopy, budget));
                writer.write(NEW_LINE);
            }
            writer.write(CSV_SEPARATOR);

            // Write all expense data
            writer.write(EXPENSE_HEADER);
            for (LeadExpense leadExpense : leadExpenses) {
                writer.write(getLeadExpenseCsv(emailCopy, leadExpense));
                writer.write(NEW_LINE);
            }
            for (TicketExpense ticketExpense : ticketExpenses) {
                writer.write(getTicketExpenseCsv(emailCopy, ticketExpense));
                writer.write(NEW_LINE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTicketExpenseCsv(String email, TicketExpense ticketExpense) {
        Ticket ticket = ticketExpense.getTicket();
        return email + "," + ticket.getSubject() + ",ticket," + ticket.getStatus() + "," + ticketExpense.getAmount();
    }

    private String getLeadExpenseCsv(String email, LeadExpense leadExpense) {
        Lead lead = leadExpense.getLead();
        return email + "," + lead.getName() + ",lead," + lead.getStatus() + "," + leadExpense.getAmount();
    }

    private String getCustomerBudgetCsv(String email, CustomerBudgetDto customerBudgetDto) {
        return email + "," + customerBudgetDto.getAmount();
    }

    private String getCustomerCsv(Customer customer) {
        return getEmailCopy(customer.getEmail()) + "," + getNameCopy(customer.getName());
    }

    private String getEmailCopy(String email) {
        return "copy_" + email;
    }

    private String getNameCopy(String name) {
        return name + " copy";
    }
}
