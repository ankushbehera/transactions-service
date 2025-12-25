package com.pismo.transactions.service.impl;

import com.pismo.transactions.domain.OperationTypeRule;
import com.pismo.transactions.dto.request.CreateTransactionRequest;
import com.pismo.transactions.dto.response.TransactionResponse;
import com.pismo.transactions.entity.Account;
import com.pismo.transactions.entity.OperationType;
import com.pismo.transactions.entity.Transaction;
import com.pismo.transactions.repository.TransactionRepository;
import com.pismo.transactions.service.AccountService;
import com.pismo.transactions.service.OperationTypeService;
import com.pismo.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final AccountService accountService;
    private final OperationTypeService operationTypeService;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionResponse create(CreateTransactionRequest request) {

        LOGGER.debug("Transaction request: {}", request);

        Account account = accountService.getEntity(request.accountId());
        OperationType operationType = operationTypeService.findEntityById(request.operationTypeId());

        LOGGER.debug("Operation id: {} description: {}",
                operationType.getOperationTypeId(), operationType.getDescription());

        OperationTypeRule rule = OperationTypeRule.fromId(operationType.getOperationTypeId());
        rule.validateAmount(request.amount());

        Transaction transaction = Transaction.builder()
                .account(account)
                .operationType(operationType)
                .amount(request.amount())
                .eventDate(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .transactionId(saved.getTransactionId())
                .accountId(saved.getAccount().getAccountId())
                .operationTypeId(saved.getOperationType().getOperationTypeId())
                .amount(saved.getAmount())
                .eventDate(saved.getEventDate())
                .build();
    }
}
