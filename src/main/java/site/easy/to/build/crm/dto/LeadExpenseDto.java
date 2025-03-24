package site.easy.to.build.crm.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadExpenseDto {
    private int leadExpenseId;
    private String customerName;
    private String leadName;
    private String description;
    private double amount;
    private Date date;
}
