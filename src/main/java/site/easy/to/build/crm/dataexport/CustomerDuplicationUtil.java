package site.easy.to.build.crm.dataexport;

import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.TicketExpense;

public class CustomerDuplicationUtil {
    private CustomerDuplicationUtil() {
    }

    public static String getTicketExpenseCsv(String email, TicketExpense ticketExpense) {
        Ticket ticket = ticketExpense.getTicket();
        return email + "," + ticket.getSubject() + ",ticket," + ticket.getStatus() + "," + ticketExpense.getAmount();
    }

    public static String getLeadExpenseCsv(String email, LeadExpense leadExpense) {
        Lead lead = leadExpense.getLead();
        return email + "," + lead.getName() + ",lead," + lead.getStatus() + "," + leadExpense.getAmount();
    }

    public static String getCustomerBudgetCsv(String email, CustomerBudgetDto customerBudgetDto) {
        return email + "," + customerBudgetDto.getAmount();
    }

    public static String getCustomerCsv(Customer customer) {
        return getEmailCopy(customer.getEmail()) + "," + getNameCopy(customer.getName());
    }

    public static String getEmailCopy(String email) {
        return "copy_" + email;
    }

    public static String getNameCopy(String name) {
        return name + " copy";
    }
}
