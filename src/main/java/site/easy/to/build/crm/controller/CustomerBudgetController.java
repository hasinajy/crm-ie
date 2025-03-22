package site.easy.to.build.crm.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.service.budget.CustomerBudgetService;

@Controller
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class CustomerBudgetController {
    private final CustomerBudgetService budgetService;

    @GetMapping({ "", "/" })
    public String showBudgets(Model model) {
        List<CustomerBudgetDto> budgets = budgetService.getAllBudgetDtos();
        model.addAttribute("budgets", budgets);
        return "customer-budget/index";
    }
}