package site.easy.to.build.crm.util;

import site.easy.to.build.crm.dto.LeadExpenseDto;
import site.easy.to.build.crm.entity.LeadExpense;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class LeadExpenseMapper {
    private LeadExpenseMapper() {
    }

    public static List<LeadExpenseDto> mapLeadExpensesToDtos(List<LeadExpense> leadExpenses) {
        return leadExpenses.stream()
                .map(LeadExpenseMapper::mapLeadExpenseToDto)
                .toList();
    }

    public static LeadExpenseDto mapLeadExpenseToDto(LeadExpense leadExpense) {
        LeadExpenseDto dto = new LeadExpenseDto();
        dto.setLeadExpenseId(leadExpense.getLeadExpenseId());

        if (leadExpense.getLead() != null && leadExpense.getLead().getCustomer() != null) {
            dto.setCustomerName(leadExpense.getLead().getCustomer().getName());
        } else {
            dto.setCustomerName("N/A");
        }

        if (leadExpense.getLead() != null) {
            dto.setLeadName(leadExpense.getLead().getName());
        } else {
            dto.setLeadName("N/A");
        }

        dto.setDescription(leadExpense.getDescription());
        dto.setAmount(leadExpense.getAmount());

        // Convert LocalDate to sql.Date
        LocalDate localDate = leadExpense.getDate();
        if (localDate != null) {
            dto.setDate(Date.valueOf(localDate));
        } else {
            dto.setDate(null);
        }

        return dto;
    }
}
