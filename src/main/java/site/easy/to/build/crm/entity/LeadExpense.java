package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "lead_expense")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lead_expense_id")
    private int leadExpenseId;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "date")
    private LocalDate date;
}