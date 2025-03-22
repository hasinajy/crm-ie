package site.easy.to.build.crm.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_budget")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_budget_id")
    private Long customerBudgetId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "amount")
    @NotNull(message = "Budget cannot be null")
    private Double amount;

    @Column(name = "start_date")
    @NotNull(message = "Start Date cannot be null")
    private Date startDate;

    @Column(name = "end_date")
    @NotNull(message = "End Date cannot be null")
    private Date endDate;
}