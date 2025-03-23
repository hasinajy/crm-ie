package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String showLeadExpenseForm(
            @RequestParam(name = "leadId", required = false) Integer leadId,
            Model model) {
        List<Lead> leads = leadService.getAllLeads();

        if (leadId != null) {
            Lead leadToUpdate = leadService.findByLeadId(leadId);
            Integer customerId = leadToUpdate.getCustomer().getCustomerId();
            boolean expenseThresholdReached = expenseService.checkIfExpenseThresholdIsReached(customerId);
            model.addAttribute("leadToUpdate", leadToUpdate);

            if (expenseThresholdReached) {
                model.addAttribute("expenseThresholdReached", true);
            }
        }

        model.addAttribute("leads", leads);
        return "expense/lead-form";
    }

    @PostMapping("/leads")
    public String createLeadExpense(
            @RequestParam("leadId") Integer leadId,
            @RequestParam("description") String description,
            @RequestParam("amount") double amount,
            @RequestParam("date") String dateString,
            @RequestParam(name = "confirm", required = false) String confirm,
            Model model) {

        try {
            LocalDate date = LocalDate.parse(dateString);

            LeadExpense leadExpense = new LeadExpense();
            Lead lead = leadService.findByLeadId(leadId);

            leadExpense.setLead(lead);
            leadExpense.setDescription(description);
            leadExpense.setAmount(amount);
            leadExpense.setDate(date);

            Integer customerId = lead.getCustomer().getCustomerId();
            boolean budgetIsExceeded = expenseService.checkIfBudgetIsExceeded(customerId, amount);

            if (budgetIsExceeded && confirm == null) {
                model.addAttribute("leadToUpdate", lead);
                model.addAttribute("leadExpense", leadExpense);
                model.addAttribute("confirmation", "Adding this expense would exceed the customer's budget");
                return "expense/lead-form";
            }

            leadExpenseService.createLeadExpense(leadExpense);

            return "redirect:/expenses/leads";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid data format");
            return "expense/lead-form";
        }
    }

    @GetMapping("/tickets/create")
    public String showTicketExpenseForm(
            @RequestParam(name = "ticketId", required = false) Integer ticketId,
            Model model) {
        List<Ticket> tickets = ticketService.getAllTickets();

        if (ticketId != null) {
            Ticket ticketToUpdate = ticketService.findByTicketId(ticketId);
            Integer customerId = ticketToUpdate.getCustomer().getCustomerId();
            boolean expenseThresholdReached = expenseService.checkIfExpenseThresholdIsReached(customerId);
            model.addAttribute("ticketToUpdate", ticketToUpdate);

            if (expenseThresholdReached) {
                model.addAttribute("expenseThresholdReached", true);
            }
        }

        model.addAttribute("tickets", tickets);
        return "expense/ticket-form";
    }

    @PostMapping("/tickets")
    public String createTicketExpense(
            @RequestParam("ticketId") Integer ticketId,
            @RequestParam("description") String description,
            @RequestParam("amount") double amount,
            @RequestParam("date") String dateString,
            @RequestParam(name = "confirm", required = false) String confirm,
            Model model) {

        try {
            LocalDate date = LocalDate.parse(dateString);

            TicketExpense ticketExpense = new TicketExpense();
            Ticket ticket = ticketService.findByTicketId(ticketId);

            ticketExpense.setTicket(ticket);
            ticketExpense.setDescription(description);
            ticketExpense.setAmount(amount);
            ticketExpense.setDate(date);

            Integer customerId = ticket.getCustomer().getCustomerId();
            boolean budgetIsExceeded = expenseService.checkIfBudgetIsExceeded(customerId, amount);

            if (budgetIsExceeded && confirm == null) {
                model.addAttribute("ticketToUpdate", ticket);
                model.addAttribute("ticketExpense", ticketExpense);
                model.addAttribute("confirmation", "Adding this expense would exceed the customer's budget");
                return "expense/ticket-form";
            }

            ticketExpenseService.createTicketExpense(ticketExpense);

            return "redirect:/expenses/tickets";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("error", "Invalid data format");
            return "expense/ticket-form";
        }
    }

    @GetMapping("/leads/edit")
    public String showEditLeadExpenseForm(@RequestParam("leadExpenseId") Integer leadExpenseId, Model model) {
        LeadExpense leadExpense = leadExpenseService.getLeadExpenseById(leadExpenseId);
        List<Lead> leads = leadService.getAllLeads();

        model.addAttribute("leadExpense", leadExpense);
        model.addAttribute("leads", leads);
        return "expense/lead-form";
    }

    @PostMapping("/leads/update")
    public String updateLeadExpense(
            @RequestParam("leadExpenseId") Integer leadExpenseId,
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

            leadExpense.setLeadExpenseId(leadExpenseId);
            leadExpense.setLead(lead);
            leadExpense.setDescription(description);
            leadExpense.setAmount(amount);
            leadExpense.setDate(date);

            LeadExpense updatedLeadExpense = leadExpenseService.updateLeadExpense(leadExpense);

            if (updatedLeadExpense == null) {
                model.addAttribute("error", "Lead expense not found");
                return "expense/lead-form";
            }

            return "redirect:/expenses/leads";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid data format");
            return "expense/lead-form";
        }
    }

    @GetMapping("/tickets/edit")
    public String showEditTicketExpenseForm(@RequestParam("ticketExpenseId") Integer ticketExpenseId, Model model) {
        TicketExpense ticketExpense = ticketExpenseService.getTicketExpenseById(ticketExpenseId);
        List<Ticket> tickets = ticketService.getAllTickets();

        if (ticketExpense == null) {
            return "redirect:/expenses/tickets";
        }

        model.addAttribute("ticketExpense", ticketExpense);
        model.addAttribute("tickets", tickets);
        return "expense/ticket-form";
    }

    @PostMapping("/tickets/update")
    public String updateTicketExpense(
            @RequestParam("ticketExpenseId") Integer ticketExpenseId,
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

            ticketExpense.setTicketExpenseId(ticketExpenseId);
            ticketExpense.setTicket(ticket);
            ticketExpense.setDescription(description);
            ticketExpense.setAmount(amount);
            ticketExpense.setDate(date);

            TicketExpense updatedTicketExpense = ticketExpenseService.updateTicketExpense(ticketExpense);

            if (updatedTicketExpense == null) {
                model.addAttribute("error", "Ticket expense not found");
                return "expense/ticket-form";
            }

            return "redirect:/expenses/tickets";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid data format");
            return "expense/ticket-form";
        }
    }

    @PostMapping("/leads/delete")
    public String deleteLeadExpense(@RequestParam("leadExpenseId") Integer leadExpenseId,
            RedirectAttributes redirectAttributes) {
        try {
            leadExpenseService.deleteLeadExpense(leadExpenseId);
            redirectAttributes.addFlashAttribute("message", "Lead expense deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete lead expense");
        }
        return "redirect:/expenses/leads";
    }

    @PostMapping("/tickets/delete")
    public String deleteTicketExpense(
            @RequestParam("ticketExpenseId") Integer ticketExpenseId,
            RedirectAttributes redirectAttributes) {

        try {
            ticketExpenseService.deleteTicketExpense(ticketExpenseId);
            redirectAttributes.addFlashAttribute("message", "Ticket expense deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete ticket expense");
        }
        return "redirect:/expenses/tickets";
    }
}