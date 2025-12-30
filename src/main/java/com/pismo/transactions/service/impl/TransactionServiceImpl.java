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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final AccountService accountService;
    private final OperationTypeService operationTypeService;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionResponse create(CreateTransactionRequest request) {

        LOGGER.debug("Transaction request: {}", request);

        Account account = accountService.getEntity(request.accountId());
        OperationType operationType = operationTypeService.findEntityById(request.operationTypeId());

        LOGGER.debug("Operation id: {} description: {}",
                operationType.getOperationTypeId(), operationType.getDescription());

        OperationTypeRule rule = OperationTypeRule.fromId(operationType.getOperationTypeId());
        rule.validateAmount(request.amount());

        //Balance logic
        //do only for type 4
            //Fetch all transaction by accountId where operationTypeId in (1,2,3) and balance < 0
            //iterate the list of tansaction ->
            //discharge the balance for list
            // save all updated transaction
        //save current transaction
            // type 1,2,3 - copy balance from request amount
            //type 4 - add the final calculated balance.
        int balanceToClear = request.amount().intValue(); //10

        if (request.operationTypeId().intValue() == 4) {
            List<Transaction> transactionToClear =
                    transactionRepository.findTransactionForClearingByAccountId(request.accountId());

            List<Transaction> clearedTransaction = new ArrayList<>();

            for (int i = 0; i < transactionToClear.size(); i++) {
                Transaction transaction = transactionToClear.get(i);
                int transactionBalance = transaction.getBalance().intValue();
                if (balanceToClear <= 0) {
                    balanceToClear = 0;
                    break;
                }
                int newBalance = transactionBalance + balanceToClear;// to add bigDesimal
                balanceToClear = balanceToClear + transactionBalance; // to add bigDesimal
                if(newBalance >= 0) {
                    transaction.setBalance(BigDecimal.ZERO);
                } else {
                    transaction.setBalance(BigDecimal.valueOf(newBalance));
                }
                clearedTransaction.add(transaction);
            }
            transactionRepository.saveAll(transactionToClear);
        }

        Transaction transaction = Transaction.builder()
                .account(account)
                .operationType(operationType)
                .amount(request.amount())
                .balance(BigDecimal.valueOf(balanceToClear))
                .eventDate(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .transactionId(saved.getTransactionId())
                .accountId(saved.getAccount().getAccountId())
                .operationTypeId(saved.getOperationType().getOperationTypeId())
                .amount(saved.getAmount())
                .balance(BigDecimal.valueOf(balanceToClear))
                .eventDate(saved.getEventDate())
                .build();
    }
}
