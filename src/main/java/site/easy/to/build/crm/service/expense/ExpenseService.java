package site.easy.to.build.crm.service.expense;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.TotalExpenseDto;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.ExpenseThreshold;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.repository.ExpenseThresholdRepository;
import site.easy.to.build.crm.service.budget.CustomerBudgetService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CustomerServiceImpl customerService;
    private final CustomerBudgetService customerBudgetService;
    private final ExpenseThresholdRepository expenseThresholdRepository;
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;

    /**
     * Checks if the remaining budget for a customer, identified by customerId, is
     * below the warning threshold.
     *
     * @param customerId The ID of the customer.
     * @return true if the remaining budget is below the warning threshold, false
     *         otherwise.
     */
    public boolean checkIfExpenseThresholdIsReached(Integer customerId) {
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer == null) {
            return false;
        }

        double customerBudget = customerBudgetService.getBudgetByCustomerId(customerId);
        double totalLeadExpenses = leadExpenseService.getLeadExpenseByCustomerId(customerId);
        double totalTicketExpenses = ticketExpenseService.getTicketExpenseByCustomerId(customerId);

        ExpenseThreshold threshold = getExpenseThreshold();
        if (threshold == null) {
            return false;
        }

        double totalExpenses = totalLeadExpenses + totalTicketExpenses;
        double expensesPercentage = totalExpenses / customerBudget;
        return expensesPercentage > threshold.getValue();
    }

    /**
     * Retrieves the first ExpenseThreshold entity from the database.
     *
     * @return The first ExpenseThreshold entity, or null if none exists.
     */
    public ExpenseThreshold getExpenseThreshold() {
        List<ExpenseThreshold> thresholds = expenseThresholdRepository.findAll();
        if (!thresholds.isEmpty()) {
            return thresholds.get(0);
        }
        return null;
    }

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

    /**
     * Checks if adding a new expense amount to the customer's total expenses would
     * exceed their budget.
     *
     * @param customerId       The ID of the customer.
     * @param newExpenseAmount The amount of the new expense.
     * @return true if the new expense would exceed the budget, false otherwise.
     */
    public boolean checkIfBudgetIsExceeded(Integer customerId, double newExpenseAmount) {
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer == null) {
            return false;
        }

        double customerBudget = customerBudgetService.getBudgetByCustomerId(customerId);
        double totalLeadExpenses = leadExpenseService.getLeadExpenseByCustomerId(customerId);
        double totalTicketExpenses = ticketExpenseService.getTicketExpenseByCustomerId(customerId);

        double totalExpenses = totalLeadExpenses + totalTicketExpenses;
        return (totalExpenses + newExpenseAmount) > customerBudget;
    }
}