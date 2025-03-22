package site.easy.to.build.crm.service.budget;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.repository.CustomerBudgetDtoRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerBudgetService {
    private final CustomerBudgetDtoRepository budgetDtoRepository;

    public List<CustomerBudgetDto> getAllBudgetDtos() {
        return budgetDtoRepository.findAllBudgetDtos();
    }

    public List<CustomerBudgetDto> getBudgetsByCustomerId(Long customerId) {
        return budgetDtoRepository.findBudgetDtosByCustomerId(customerId);
    }
}