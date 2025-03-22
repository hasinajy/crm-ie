package site.easy.to.build.crm.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerBudget;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.service.budget.CustomerBudgetService;

@Controller
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class CustomerBudgetController {
    private final CustomerRepository customerRepository;
    private final CustomerBudgetService budgetService;

    @GetMapping({ "", "/" })
    public String showBudgets(Model model) {
        List<CustomerBudgetDto> budgets = budgetService.getAllBudgetDtos();
        model.addAttribute("budgets", budgets);
        return "customer-budget/index";
    }

    @GetMapping("/create")
    public String displayBudgetForm(Model model) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("error", null);
        return "customer-budget/form";
    }

    @PostMapping
    public String createBudget(
            @RequestParam("customerId") Integer customerId,
            @RequestParam("amount") Double amount,
            @RequestParam("startDate") Date startDate,
            @RequestParam("endDate") Date endDate,
            Model model) {

        try {
            Customer customer = customerRepository.findByCustomerId(customerId);
            if (customer == null) {
                throw new IllegalArgumentException("Customer not found");
            }

            CustomerBudget budget = new CustomerBudget();
            budget.setCustomer(customer);
            budget.setAmount(amount);
            budget.setStartDate(startDate);
            budget.setEndDate(endDate);
            budgetService.createBudget(customerId, amount, startDate, endDate);

            return "redirect:/budgets";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while creating the budget");
            return "customer-budget/form";
        }
    }

    @GetMapping("/edit")
    public String displayEditBudgetForm(
            @RequestParam("budgetId") Integer budgetId,
            Model model) {
        CustomerBudget budget = budgetService.findById(budgetId);
        if (budget == null) {
            return "redirect:/budgets";
        }

        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("budget", budget);
        model.addAttribute("customers", customers);
        model.addAttribute("error", null);
        return "customer-budget/form";
    }

    @PostMapping("/update")
    public String updateBudget(
            @RequestParam("budgetId") Integer budgetId,
            @RequestParam("customerId") Integer customerId,
            @RequestParam("amount") Double amount,
            @RequestParam("startDate") Date startDate,
            @RequestParam("endDate") Date endDate,
            Model model) {
        try {
            Customer customer = customerRepository.findByCustomerId(customerId);
            if (customer == null) {
                throw new IllegalArgumentException("Customer not found");
            }

            CustomerBudget budget = budgetService.findById(budgetId);
            if (budget == null) {
                throw new IllegalArgumentException("Budget not found");
            }

            budget.setCustomer(customer);
            budget.setAmount(amount);
            budget.setStartDate(startDate);
            budget.setEndDate(endDate);
            budgetService.updateBudget(budget);

            return "redirect:/budgets";
        } catch (Exception e) {
            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            model.addAttribute("error", "An error occurred while updating the budget");
            model.addAttribute("budget", new CustomerBudget(budgetId, customer, amount, startDate, endDate));
            model.addAttribute("customers", customerRepository.findAll());
            return "customer-budget/form";
        }
    }

    @PostMapping("/delete")
    public String deleteBudget(
            @RequestParam("budgetId") Integer budgetId) {
        budgetService.deleteBudget(budgetId);
        return "redirect:/budgets";
    }
}