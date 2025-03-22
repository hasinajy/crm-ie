package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.TotalExpenseDto;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.service.expense.ExpenseService;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;
    private final ExpenseService expenseService;
    private final LeadServiceImpl leadService;
    private final TicketServiceImpl ticketService;

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

    @GetMapping("/leads/create")
    public String showLeadExpenseForm(Model model) {
        List<Lead> leads = leadService.getAllLeads();
        model.addAttribute("leads", leads);
        return "expense/lead-form";
    }

    @PostMapping("/leads")
    public String createLeadExpense(
            @RequestParam("leadId") Integer leadId,
            @RequestParam("description") String description,
            @RequestParam("amount") double amount,
            @RequestParam("date") String dateString,
            Model model) {

        try {
            LocalDate date = LocalDate.parse(dateString);

            LeadExpense leadExpense = new LeadExpense();
            Lead lead = new Lead();
            lead.setLeadId(leadId);

            leadExpense.setLead(lead);
            leadExpense.setDescription(description);
            leadExpense.setAmount(amount);
            leadExpense.setDate(date);

            leadExpenseService.createLeadExpense(leadExpense);

            return "redirect:/expenses/leads";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid data format");
            return "expense/lead-form";
        }
    }

    @GetMapping("tickets/create")
    public String showTicketExpenseForm(Model model) {
        List<Ticket> tickets = ticketService.getAllTickets();
        model.addAttribute("tickets", tickets);
        return "expense/ticket-form";
    }

    @PostMapping("/tickets")
    public String createTicketExpense(
            @RequestParam("ticketId") Integer ticketId,
            @RequestParam("description") String description,
            @RequestParam("amount") double amount,
            @RequestParam("date") String dateString,
            Model model) {

        try {
            LocalDate date = LocalDate.parse(dateString);

            TicketExpense ticketExpense = new TicketExpense();
            Ticket ticket = new Ticket();
            ticket.setTicketId(ticketId);

            ticketExpense.setTicket(ticket);
            ticketExpense.setDescription(description);
            ticketExpense.setAmount(amount);
            ticketExpense.setDate(date);

            ticketExpenseService.createTicketExpense(ticketExpense);

            return "redirect:/expenses/tickets";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid data format");
            return "expense/ticket-form";
        }
    }
}