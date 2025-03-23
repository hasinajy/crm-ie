package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expense_threshold")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "threshold_id")
    private Integer thresholdId;

    @Column(name = "value")
    private Double value;
}