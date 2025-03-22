package site.easy.to.build.crm.repository;

import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.entity.CustomerBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface CustomerBudgetRepository extends JpaRepository<CustomerBudget, Long> {
    @Query("SELECT cb FROM CustomerBudget cb WHERE cb.customerBudgetId = :customerBudgetId")
    CustomerBudget findByCustomerBudgetId(@Param("customerBudgetId") Integer customerBudgetId);

    @Query("SELECT new site.easy.to.build.crm.dto.CustomerBudgetDto(" +
            "cb.customerBudgetId, " +
            "c.name, " +
            "cb.amount, " +
            "cb.startDate, " +
            "cb.endDate) " +
            "FROM CustomerBudget cb JOIN cb.customer c")
    List<CustomerBudgetDto> findAllBudgetDtos();

    @Query("SELECT new site.easy.to.build.crm.dto.CustomerBudgetDto(" +
            "cb.customerBudgetId, " +
            "c.name, " +
            "cb.amount, " +
            "cb.startDate, " +
            "cb.endDate) " +
            "FROM CustomerBudget cb JOIN cb.customer c " +
            "WHERE c.id = :customerId")
    List<CustomerBudgetDto> findBudgetDtosByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT cb FROM CustomerBudget cb " +
            "WHERE cb.customer.customerId = :customerId " +
            "AND ((cb.startDate <= :endDate AND cb.endDate >= :startDate))")
    List<CustomerBudget> findOverlappingBudgets(
            @Param("customerId") Integer customerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT cb FROM CustomerBudget cb " +
            "WHERE cb.customer.customerId = :customerId " +
            "AND cb.startDate <= :date AND cb.endDate >= :date")
    List<CustomerBudget> findActiveBudgetsOnDate(
            @Param("customerId") Long customerId,
            @Param("date") Date date);
}