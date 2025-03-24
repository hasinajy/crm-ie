package site.easy.to.build.crm.api;

import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.entity.ExpenseThreshold;
import site.easy.to.build.crm.service.expense.ExpenseService;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExpenseThresholdApiController {
    private final ExpenseService expenseService;

    @GetMapping(value = "/v1/expense-threshold", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getExpenseThreshold() {
        Gson gson = new Gson();
        ExpenseThreshold expenseThreshold = expenseService.getExpenseThreshold();

        return gson.toJson(expenseThreshold.getValue());
    }

    @PutMapping(value = "/v1/expense-threshold", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateExpenseThreshold(@RequestBody Map<String, Object> requestBody) {
        try {
            if (!requestBody.containsKey("value")) {
                return ResponseEntity.badRequest().body("Value is required.");
            }

            double newValue = ((Number) requestBody.get("value")).doubleValue();
            expenseService.updateExpenseThreshold(newValue);

            Gson gson = new Gson();
            return ResponseEntity.ok(gson.toJson(newValue));
        } catch (ClassCastException e) {
            return ResponseEntity.badRequest().body("Invalid data format for value.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating expense threshold");
        }
    }
}
