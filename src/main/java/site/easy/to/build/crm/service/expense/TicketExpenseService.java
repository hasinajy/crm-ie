package site.easy.to.build.crm.service.expense;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.repository.expense.TicketExpenseRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketExpenseService {
    private final TicketExpenseRepository ticketExpenseRepository;

    public List<TicketExpense> getAllTicketExpenses() {
        return ticketExpenseRepository.findAll();
    }

    public List<TicketExpense> getAllTicketExpensesByCustomerId(Integer customerId) {
        return ticketExpenseRepository.findAllTicketExpensesByCustomerId(customerId);
    }

    public TicketExpense createTicketExpense(TicketExpense ticketExpense) {
        return ticketExpenseRepository.save(ticketExpense);
    }
}