package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.TotalExpenseDto;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.service.expense.ExpenseService;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;

import java.util.List;

@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;
    private final ExpenseService expenseService;

    @GetMapping("/leads")
    public String displayLeadExpenses(@RequestParam(value = "customerId", required = false) Integer customerId,
            Model model) {
        List<LeadExpense> leadExpenses;

        if (customerId != null) {
            leadExpenses = leadExpenseService.getAllLeadExpensesByCustomerId(customerId);
        } else {
            leadExpenses = leadExpenseService.getAllLeadExpenses();
        }

        model.addAttribute("leadExpenses", leadExpenses);
        return "expense/lead-index";
    }

    @GetMapping("/tickets")
    public String displayTicketExpenses(@RequestParam(value = "customerId", required = false) Integer customerId,
            Model model) {
        List<TicketExpense> ticketExpenses;

        if (customerId != null) {
            ticketExpenses = ticketExpenseService.getAllTicketExpensesByCustomerId(customerId);
        } else {
            ticketExpenses = ticketExpenseService.getAllTicketExpenses();
        }

        model.addAttribute("ticketExpenses", ticketExpenses);
        return "expense/ticket-index";
    }

    @GetMapping("/totals")
    public String displayTotalExpenses(Model model) {
        List<TotalExpenseDto> totalExpenses = expenseService.getAllTotalExpenses();
        model.addAttribute("totalExpenses", totalExpenses);
        return "expense/total-index";
    }
}