package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.ExpenseThreshold;

import java.util.List;

@Repository
public interface ExpenseThresholdRepository extends JpaRepository<ExpenseThreshold, Integer> {
    List<ExpenseThreshold> findAll();
}