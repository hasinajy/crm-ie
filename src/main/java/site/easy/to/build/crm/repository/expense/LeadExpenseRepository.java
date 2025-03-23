package site.easy.to.build.crm.repository.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.LeadExpense;

import java.util.List;

@Repository
public interface LeadExpenseRepository extends JpaRepository<LeadExpense, Integer> {
    public List<LeadExpense> findAll();

    @Query("SELECT le FROM LeadExpense le JOIN le.lead l JOIN l.customer c WHERE c.customerId = :customerId")
    public List<LeadExpense> findAllLeadExpensesByCustomerId(@Param("customerId") Integer customerId);
}