package site.easy.to.build.crm.util.mapper;

import site.easy.to.build.crm.dto.TicketExpenseDto;
import site.easy.to.build.crm.entity.TicketExpense;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class TicketExpenseMapper {
    private TicketExpenseMapper() {
    }

    public static List<TicketExpenseDto> mapTicketExpensesToDtos(List<TicketExpense> ticketExpenses) {
        return ticketExpenses.stream()
                .map(TicketExpenseMapper::mapTicketExpenseToDto)
                .toList();
    }

    public static TicketExpenseDto mapTicketExpenseToDto(TicketExpense ticketExpense) {
        TicketExpenseDto dto = new TicketExpenseDto();
        dto.setTicketExpenseId(ticketExpense.getTicketExpenseId());

        if (ticketExpense.getTicket() != null && ticketExpense.getTicket().getCustomer() != null) {
            dto.setCustomerName(ticketExpense.getTicket().getCustomer().getName());
        } else {
            dto.setCustomerName("N/A");
        }

        if (ticketExpense.getTicket() != null) {
            dto.setTicketName(ticketExpense.getTicket().getSubject());
        } else {
            dto.setTicketName("N/A");
        }

        dto.setDescription(ticketExpense.getDescription());
        dto.setAmount(ticketExpense.getAmount());

        // Convert LocalDate to sql.Date
        LocalDate localDate = ticketExpense.getDate();
        if (localDate != null) {
            dto.setDate(Date.valueOf(localDate));
        } else {
            dto.setDate(null);
        }

        return dto;
    }
}