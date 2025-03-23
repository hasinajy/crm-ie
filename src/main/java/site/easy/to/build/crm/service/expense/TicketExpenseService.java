package site.easy.to.build.crm.service.expense;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.repository.expense.TicketExpenseRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketExpenseService {
    private final TicketExpenseRepository ticketExpenseRepository;

    public TicketExpense getTicketExpenseById(Integer ticketExpenseId) {
        return ticketExpenseRepository.findById(ticketExpenseId).orElseThrow();
    }

    public List<TicketExpense> getAllTicketExpenses() {
        return ticketExpenseRepository.findAll();
    }

    public List<TicketExpense> getAllTicketExpensesByCustomerId(Integer customerId) {
        return ticketExpenseRepository.findAllTicketExpensesByCustomerId(customerId);
    }

    public TicketExpense createTicketExpense(TicketExpense ticketExpense) {
        return ticketExpenseRepository.save(ticketExpense);
    }

    public TicketExpense updateTicketExpense(TicketExpense ticketExpense) {
        Optional<TicketExpense> existingTicketExpense = ticketExpenseRepository
                .findById(ticketExpense.getTicketExpenseId());

        if (existingTicketExpense.isPresent()) {
            return ticketExpenseRepository.save(ticketExpense);
        } else {
            throw new IllegalStateException(
                    "Ticket expense with id " + ticketExpense.getTicketExpenseId() + " does not exist");
        }
    }

    public void deleteTicketExpense(Integer ticketExpenseId) {
        ticketExpenseRepository.deleteById(ticketExpenseId);
    }
}