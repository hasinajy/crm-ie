package site.easy.to.build.crm.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class TicketExpenseDto {
    private int ticketExpenseId;
    private String customerName;
    private String ticketName;
    private String description;
    private double amount;
    private Date date;
}
