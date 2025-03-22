package site.easy.to.build.crm.service.expense;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.TotalExpenseDto;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.TicketExpense;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;

    /**
     * Aggregates lead and ticket expenses to calculate total expenses for each
     * customer.
     *
     * @return A list of TotalExpenseDto objects containing aggregated expense data.
     */
    public List<TotalExpenseDto> getAllTotalExpenses() {
        List<LeadExpense> allLeadExpenses = leadExpenseService.getAllLeadExpenses();
        List<TicketExpense> allTicketExpenses = ticketExpenseService.getAllTicketExpenses();

        // Group expenses by customer and calculate totals
        return allLeadExpenses.stream()
                .collect(Collectors.groupingBy(leadExpense -> leadExpense.getLead().getCustomer().getName()))
                .entrySet().stream()
                .map(entry -> {
                    String customerName = entry.getKey();
                    double totalLead = entry.getValue().stream().mapToDouble(LeadExpense::getAmount).sum();
                    double totalTicket = allTicketExpenses.stream()
                            .filter(ticketExpense -> ticketExpense.getTicket().getCustomer().getName()
                                    .equals(customerName))
                            .mapToDouble(TicketExpense::getAmount)
                            .sum();
                    TotalExpenseDto dto = new TotalExpenseDto();
                    dto.setCustomerName(customerName);
                    dto.setTotalLeadExpenses(totalLead);
                    dto.setTotalTicketExpenses(totalTicket);
                    dto.setTotalExpenses(totalLead + totalTicket);
                    return dto;
                })
                .toList();
    }
}