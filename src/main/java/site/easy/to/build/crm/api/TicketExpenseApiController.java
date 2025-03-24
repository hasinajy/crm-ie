package site.easy.to.build.crm.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping(value = "/v1/ticket-expenses", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllTicketExpenses() {
        List<TicketExpense> ticketExpenses = ticketExpenseService.getAllTicketExpenses();
        List<TicketExpenseDto> ticketExpenseDtos = TicketExpenseMapper.mapTicketExpensesToDtos(ticketExpenses);
        Gson gson = new Gson();
        return gson.toJson(ticketExpenseDtos);
    }
}
