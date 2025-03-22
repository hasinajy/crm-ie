package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "ticket_expense")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_expense_id")
    private int ticketExpenseId;

    @ManyToOne
    @JoinColumn(name = "trigger_ticket_id")
    private Ticket ticket;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "date")
    private LocalDate date;

    @Override
    public String toString() {
        return "TicketExpense{" +
                "ticketExpenseId=" + ticketExpenseId +
                ", ticket=" + ticket +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}