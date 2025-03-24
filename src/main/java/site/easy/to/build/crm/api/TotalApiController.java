package site.easy.to.build.crm.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.service.budget.CustomerBudgetService;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.service.expense.TicketExpenseService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TotalApiController {
    private final CustomerBudgetService budgetService;
    private final LeadExpenseService leadExpenseService;
    private final TicketExpenseService ticketExpenseService;

    @GetMapping(value = "/v1/totals", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTotals() {
        double budgetsTotal = budgetService.getBudgetsTotal();
        double leadExpensesTotal = leadExpenseService.getLeadExpensesTotal();
        double ticketExpensesTotal = ticketExpenseService.getTicketExpensesTotal();

        JsonObject totalsObject = new JsonObject();
        totalsObject.addProperty("budgets-total", budgetsTotal);
        totalsObject.addProperty("lead-expenses-total", leadExpensesTotal);
        totalsObject.addProperty("ticket-expenses-total", ticketExpensesTotal);

        Gson gson = new Gson();
        return gson.toJson(totalsObject);
    }
}
