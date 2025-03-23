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

    /**
     * Retrieves the sum of all ticket expense amounts for a customer by their ID.
     *
     * @param customerId The ID of the customer.
     * @return The sum of all ticket expense amounts, or 0.0 if no ticket expenses
     *         are found.
     */
    public double getTicketExpenseByCustomerId(Integer customerId) {
        List<TicketExpense> ticketExpenses = ticketExpenseRepository.findAllTicketExpensesByCustomerId(customerId);
        if (ticketExpenses != null && !ticketExpenses.isEmpty()) {
            return ticketExpenses.stream().mapToDouble(TicketExpense::getAmount).sum();
        }
        return 0.0;
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