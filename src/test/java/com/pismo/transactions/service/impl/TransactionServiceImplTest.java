package com.pismo.transactions.service.impl;

import com.pismo.transactions.dto.request.CreateTransactionRequest;
import com.pismo.transactions.dto.response.TransactionResponse;
import com.pismo.transactions.entity.Account;
import com.pismo.transactions.entity.OperationType;
import com.pismo.transactions.entity.Transaction;
import com.pismo.transactions.exception.InvalidTransactionException;
import com.pismo.transactions.repository.TransactionRepository;
import com.pismo.transactions.service.AccountService;
import com.pismo.transactions.service.OperationTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private OperationTypeService operationTypeService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl service;

    @Test
    void create_ShouldCreateTransaction_WhenValidRequest() {
        CreateTransactionRequest request =
                new CreateTransactionRequest(10L, 1, BigDecimal.valueOf(-100.56));

        Account acc = new Account();
        acc.setAccountId(10L);

        OperationType op = new OperationType();
        op.setOperationTypeId(1);

        Transaction saved = Transaction.builder()
                .transactionId(99L)
                .account(acc)
                .operationType(op)
                .amount(BigDecimal.valueOf(-100.56))
                .eventDate(LocalDateTime.now())
                .build();

        when(accountService.getEntity(10L)).thenReturn(acc);
        when(operationTypeService.findEntityById(1)).thenReturn(op);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponse response = service.create(request);

        assertEquals(99L, response.transactionId());
        assertEquals(10L, response.accountId());
        assertEquals(1, response.operationTypeId());
        assertEquals(BigDecimal.valueOf(-100.56), response.amount());
        assertNotNull(response.eventDate());

        verify(accountService).getEntity(10L);
        verify(operationTypeService).findEntityById(1);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void create_ShouldThrow_WhenRuleValidationFails() {
        CreateTransactionRequest request =
                new CreateTransactionRequest(10L, 1, BigDecimal.ZERO);

        Account acc = new Account();
        acc.setAccountId(10L);

        OperationType op = new OperationType();
        op.setOperationTypeId(1);

        when(accountService.getEntity(10L)).thenReturn(acc);
        when(operationTypeService.findEntityById(1)).thenReturn(op);

        assertThrows(InvalidTransactionException.class,
                () -> service.create(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_ShouldPropagate_WhenAccountNotFound() {
        CreateTransactionRequest request =
                new CreateTransactionRequest(99L, 1, BigDecimal.valueOf(10));

        when(accountService.getEntity(99L))
                .thenThrow(new RuntimeException("Account missing"));

        assertThrows(RuntimeException.class, () -> service.create(request));

        verify(operationTypeService, never()).findEntityById(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_ShouldPropagate_WhenOperationTypeNotFound() {
        CreateTransactionRequest request =
                new CreateTransactionRequest(10L, 5, BigDecimal.valueOf(10));

        Account acc = new Account();
        acc.setAccountId(10L);

        when(accountService.getEntity(10L)).thenReturn(acc);
        when(operationTypeService.findEntityById(5))
                .thenThrow(new RuntimeException("Op missing"));

        assertThrows(RuntimeException.class, () -> service.create(request));

        verify(transactionRepository, never()).save(any());
    }
}
