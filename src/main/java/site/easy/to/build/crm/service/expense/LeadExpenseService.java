package site.easy.to.build.crm.service.expense;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.repository.expense.LeadExpenseRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeadExpenseService {
    private final LeadExpenseRepository leadExpenseRepository;

    public LeadExpense getLeadExpenseById(Integer expenseId) {
        return leadExpenseRepository.findById(expenseId).orElseThrow();
    }

    public List<LeadExpense> getAllLeadExpenses() {
        return leadExpenseRepository.findAll();
    }

    public List<LeadExpense> getAllLeadExpensesByCustomerId(Integer customerId) {
        return leadExpenseRepository.findAllLeadExpensesByCustomerId(customerId);
    }

    public LeadExpense createLeadExpense(LeadExpense leadExpense) {
        return leadExpenseRepository.save(leadExpense);
    }

    public LeadExpense updateLeadExpense(LeadExpense leadExpense) {
        Optional<LeadExpense> existingLeadExpense = leadExpenseRepository.findById(leadExpense.getLeadExpenseId());

        if (existingLeadExpense.isPresent()) {
            return leadExpenseRepository.save(leadExpense);
        } else {
            throw new IllegalStateException(
                    "Lead expense with id " + leadExpense.getLeadExpenseId() + " does not exist");
        }
    }

    public void deleteLeadExpense(Integer leadExpenseId) {
        leadExpenseRepository.deleteById(leadExpenseId);
    }
}