package site.easy.to.build.crm.dto;

import lombok.Data;

@Data
public class TotalExpenseDto {
    private String customerName;
    private Double totalExpenses;
    private Double totalLeadExpenses;
    private Double totalTicketExpenses;
}