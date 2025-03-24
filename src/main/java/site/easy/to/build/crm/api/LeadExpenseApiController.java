package site.easy.to.build.crm.api;

import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.LeadExpenseDto;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.util.mapper.LeadExpenseMapper;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LeadExpenseApiController {
    private final LeadExpenseService leadExpenseService;
    private final LeadExpenseMapper leadExpenseMapper;

    @GetMapping(value = "/v1/lead-expenses", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllLeadExpenses() {
        List<LeadExpense> leadExpenses = leadExpenseService.getAllLeadExpenses();
        List<LeadExpenseDto> leadExpenseDtos = leadExpenseMapper.mapLeadExpensesToDtos(leadExpenses);
        Gson gson = new Gson();
        return gson.toJson(leadExpenseDtos);
    }

    @PutMapping(value = "/v1/lead-expenses/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateLeadExpense(
            @PathVariable("id") int id,
            @RequestBody Map<String, Object> requestBody) {

        try {
            String description = (String) requestBody.get("description");
            double amount = Double.parseDouble((String) requestBody.get("amount"));

            LeadExpense leadExpense = leadExpenseService.getLeadExpenseById(id);
            leadExpense.setDescription(description);
            leadExpense.setAmount(amount);

            LeadExpense updatedLeadExpense = leadExpenseService.updateLeadExpense(leadExpense);
            if (updatedLeadExpense == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error updating lead expense");
            }

            LeadExpenseDto updatedDto = leadExpenseMapper.mapLeadExpenseToDto(updatedLeadExpense);
            Gson gson = new Gson();
            return ResponseEntity.ok(gson.toJson(updatedDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating lead expense");
        }
    }

    @DeleteMapping("/v1/lead-expenses/{id}")
    public ResponseEntity<String> deleteLeadExpense(@PathVariable("id") int id) {
        try {
            leadExpenseService.deleteLeadExpense(id);
            return ResponseEntity.ok("Lead expense deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting lead expense");
        }
    }

}
