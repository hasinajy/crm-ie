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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        // Create maps of totals by customer name
        Map<String, Double> leadTotals = allLeadExpenses.stream()
                .collect(Collectors.groupingBy(
                        leadExpense -> leadExpense.getLead().getCustomer().getName(),
                        Collectors.summingDouble(LeadExpense::getAmount)));

        Map<String, Double> ticketTotals = allTicketExpenses.stream()
                .collect(Collectors.groupingBy(
                        ticketExpense -> ticketExpense.getTicket().getCustomer().getName(),
                        Collectors.summingDouble(TicketExpense::getAmount)));

        // Get all unique customer names from both lead and ticket expenses
        Set<String> allCustomers = new HashSet<>();
        allCustomers.addAll(leadTotals.keySet());
        allCustomers.addAll(ticketTotals.keySet());

        // Create DTOs for all customers
        return allCustomers.stream()
                .map(customerName -> {
                    double totalLead = leadTotals.getOrDefault(customerName, 0.0);
                    double totalTicket = ticketTotals.getOrDefault(customerName, 0.0);
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

    /**
     * Updates the existing ExpenseThreshold value or creates a new one if it
     * doesn't exist.
     *
     * @param newValue The new threshold value to set.
     * @return The updated ExpenseThreshold entity.
     */
    public ExpenseThreshold updateExpenseThreshold(double newValue) {
        List<ExpenseThreshold> thresholds = expenseThresholdRepository.findAll();
        ExpenseThreshold threshold;
        if (!thresholds.isEmpty()) {
            threshold = thresholds.get(0);
            threshold.setValue(newValue);
        } else {
            threshold = new ExpenseThreshold();
            threshold.setValue(newValue);
        }
        return expenseThresholdRepository.save(threshold);
    }
}