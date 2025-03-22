package site.easy.to.build.crm.repository;

import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.entity.CustomerBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerBudgetRepository extends JpaRepository<CustomerBudget, Long> {

    @Query("SELECT new site.easy.to.build.crm.dto.CustomerBudgetDto(" +
            "c.name, " +
            "cb.amount, " +
            "cb.startDate, " +
            "cb.endDate) " +
            "FROM CustomerBudget cb JOIN cb.customer c")
    List<CustomerBudgetDto> findAllBudgetDtos();

    @Query("SELECT new site.easy.to.build.crm.dto.CustomerBudgetDto(" +
            "c.name, " +
            "cb.amount, " +
            "cb.startDate, " +
            "cb.endDate) " +
            "FROM CustomerBudget cb JOIN cb.customer c " +
            "WHERE c.id = :customerId")
    List<CustomerBudgetDto> findBudgetDtosByCustomerId(@Param("customerId") Long customerId);
}