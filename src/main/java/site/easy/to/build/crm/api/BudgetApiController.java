package site.easy.to.build.crm.api;

import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.entity.CustomerBudget;
import site.easy.to.build.crm.service.budget.CustomerBudgetService;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BudgetApiController {
    private final CustomerBudgetService customerBudgetService;

    @GetMapping(value = "/v1/budgets", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllBudgets() {
        List<CustomerBudgetDto> allBudgets = customerBudgetService.getAllBudgetDtos();
        Gson gson = new Gson();
        return gson.toJson(allBudgets);
    }

    @PutMapping(value = "/v1/budgets/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCustomerBudget(
            @PathVariable("id") int id,
            @RequestBody Map<String, Object> requestBody) {

        try {
            double amount = Double.parseDouble((String) requestBody.get("amount"));

            if (amount < 0) {
                return ResponseEntity.badRequest().body("Amount must be greater than or equal to 0.");
            }

            CustomerBudget customerBudget = customerBudgetService.findById(id);
            customerBudget.setAmount(amount);

            CustomerBudget updatedCustomerBudget = customerBudgetService.updateBudget(customerBudget);
            if (updatedCustomerBudget == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error updating customer budget");
            }

            return ResponseEntity.ok("Customer budget updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating customer budget");
        }
    }

    @DeleteMapping("/v1/budgets/{id}")
    public ResponseEntity<String> deleteCustomerBudget(@PathVariable("id") int id) {
        try {
            customerBudgetService.deleteBudget(id);
            return ResponseEntity.ok("Customer budget deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting customer budget");
        }
    }
}
