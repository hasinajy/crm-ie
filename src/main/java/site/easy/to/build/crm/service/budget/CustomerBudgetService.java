package site.easy.to.build.crm.service.budget;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerBudget;
import site.easy.to.build.crm.repository.CustomerBudgetRepository;

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerBudgetService {
    private final CustomerBudgetRepository customerBudgetRepository;

    public List<CustomerBudgetDto> getAllBudgetDtos() {
        return customerBudgetRepository.findAllBudgetDtos();
    }

    public List<CustomerBudgetDto> getBudgetsByCustomerId(Integer customerId) {
        return customerBudgetRepository.findBudgetDtosByCustomerId(customerId);
    }

    public CustomerBudget createBudget(Integer customerId, Double amount, Date startDate, Date endDate) {
        // Check for overlaps
        List<CustomerBudget> overlaps = customerBudgetRepository.findOverlappingBudgets(customerId, startDate, endDate);
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Overlapping budget exists");
        }

        CustomerBudget budget = new CustomerBudget();
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        budget.setCustomer(customer);
        budget.setAmount(amount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);

        return customerBudgetRepository.save(budget);
    }

    public CustomerBudget findById(Integer customerBudgetId) {
        return customerBudgetRepository.findByCustomerBudgetId(customerBudgetId);
    }

    public void updateBudget(CustomerBudget budget) {
        List<CustomerBudget> overlaps = customerBudgetRepository.findOverlappingBudgets(
                budget.getCustomer().getCustomerId(), budget.getStartDate(), budget.getEndDate());
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Overlapping budget exists");
        }
        customerBudgetRepository.save(budget);
    }

    public void deleteBudget(Integer budgetId) {
        customerBudgetRepository.deleteById(budgetId.longValue());
    }

    /**
     * Retrieves the sum of all budget amounts for a customer by their ID.
     *
     * @param customerId The ID of the customer.
     * @return The sum of all budget amounts, or 0.0 if no budgets are found.
     */
    public double getBudgetByCustomerId(Integer customerId) {
        List<CustomerBudgetDto> budgets = customerBudgetRepository.findBudgetDtosByCustomerId(customerId);
        if (budgets != null && !budgets.isEmpty()) {
            return budgets.stream().mapToDouble(CustomerBudgetDto::getAmount).sum();
        }
        return 0.0;
    }
}