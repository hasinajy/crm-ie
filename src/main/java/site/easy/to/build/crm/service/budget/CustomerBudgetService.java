package site.easy.to.build.crm.service.budget;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.dto.CustomerBudgetDto;
import site.easy.to.build.crm.repository.CustomerBudgetRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerBudgetService {
    private final CustomerBudgetRepository customerBudgetRepository;

    public List<CustomerBudgetDto> getAllBudgetDtos() {
        return customerBudgetRepository.findAllBudgetDtos();
    }

    public List<CustomerBudgetDto> getBudgetsByCustomerId(Long customerId) {
        return customerBudgetRepository.findBudgetDtosByCustomerId(customerId);
    }
}