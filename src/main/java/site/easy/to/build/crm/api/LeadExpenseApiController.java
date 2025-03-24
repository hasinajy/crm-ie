package site.easy.to.build.crm.api;

import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.LeadExpenseDto;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.service.expense.LeadExpenseService;
import site.easy.to.build.crm.util.mapper.LeadExpenseMapper;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LeadExpenseApiController {
    private final LeadExpenseService leadExpenseService;

    @GetMapping(value = "/v1/lead-expenses", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllLeadExpenses() {
        List<LeadExpense> leadExpenses = leadExpenseService.getAllLeadExpenses();
        List<LeadExpenseDto> leadExpenseDtos = LeadExpenseMapper.mapLeadExpensesToDtos(leadExpenses);
        Gson gson = new Gson();
        return gson.toJson(leadExpenseDtos);
    }

}
