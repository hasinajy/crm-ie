package site.easy.to.build.crm.repository.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.TicketExpense;

import java.util.List;

@Repository
public interface TicketExpenseRepository extends JpaRepository<TicketExpense, Integer> {
    public List<TicketExpense> findAll();

    @Query("SELECT te FROM TicketExpense te JOIN te.ticket t JOIN t.customer c WHERE c.customerId = :customerId")
    public List<TicketExpense> findAllTicketExpensesByCustomerId(@Param("customerId") Integer customerId);
}