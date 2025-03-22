package site.easy.to.build.crm.service.expense;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.repository.expense.LeadExpenseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadExpenseService {
    private final LeadExpenseRepository leadExpenseRepository;

    /**
     * Retrieves all lead expenses.
     *
     * @return A list of all LeadExpense objects.
     */
    public List<LeadExpense> getAllLeadExpenses() {
        return leadExpenseRepository.findAll();
    }

    /**
     * Retrieves all lead expenses associated with a specific customer ID.
     *
     * @param customerId The ID of the customer.
     * @return A list of LeadExpense objects related to the specified customer.
     */
    public List<LeadExpense> getAllLeadExpensesByCustomerId(Integer customerId) {
        return leadExpenseRepository.findAllLeadExpensesByCustomerId(customerId);
    }
}