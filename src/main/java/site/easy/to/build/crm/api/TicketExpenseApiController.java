package site.easy.to.build.crm.api;

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
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.TicketExpenseDto;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.service.expense.TicketExpenseService;
import site.easy.to.build.crm.util.mapper.TicketExpenseMapper;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketExpenseApiController {
    private final TicketExpenseService ticketExpenseService;
    private final TicketExpenseMapper ticketExpenseMapper;

    @GetMapping(value = "/v1/ticket-expenses", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllTicketExpenses() {
        List<TicketExpense> ticketExpenses = ticketExpenseService.getAllTicketExpenses();
        List<TicketExpenseDto> ticketExpenseDtos = ticketExpenseMapper.mapTicketExpensesToDtos(ticketExpenses);
        Gson gson = new Gson();
        return gson.toJson(ticketExpenseDtos);
    }

    @PutMapping(value = "/v1/ticket-expenses/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateTicketExpense(
            @PathVariable("id") int id,
            @RequestBody Map<String, Object> requestBody) {

        try {
            String description = (String) requestBody.get("description");
            double amount = Double.parseDouble((String) requestBody.get("amount"));

            if (amount < 0) {
                return ResponseEntity.badRequest().body("Amount must be greater than or equal to 0.");
            }

            if (Double.valueOf(description).isNaN()) {
                return ResponseEntity.badRequest().body("Amount must be a number.");
            }

            TicketExpense ticketExpense = ticketExpenseService.getTicketExpenseById(id);
            ticketExpense.setDescription(description);
            ticketExpense.setAmount(amount);

            TicketExpense updatedTicketExpense = ticketExpenseService.updateTicketExpense(ticketExpense);
            if (updatedTicketExpense == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error updating ticket expense");
            }

            TicketExpenseDto updatedDto = ticketExpenseMapper.mapTicketExpenseToDto(updatedTicketExpense);
            Gson gson = new Gson();
            return ResponseEntity.ok(gson.toJson(updatedDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating ticket expense");
        }
    }

    @DeleteMapping("/v1/ticket-expenses/{id}")
    public ResponseEntity<String> deleteTicketExpense(@PathVariable("id") int id) {
        try {
            ticketExpenseService.deleteTicketExpense(id);
            return ResponseEntity.ok("Ticket expense deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting ticket expense");
        }
    }
}
