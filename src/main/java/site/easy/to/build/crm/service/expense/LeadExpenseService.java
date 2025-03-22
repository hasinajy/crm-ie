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

    public List<LeadExpense> getAllLeadExpenses() {
        return leadExpenseRepository.findAll();
    }

    public List<LeadExpense> getAllLeadExpensesByCustomerId(Integer customerId) {
        return leadExpenseRepository.findAllLeadExpensesByCustomerId(customerId);
    }
}