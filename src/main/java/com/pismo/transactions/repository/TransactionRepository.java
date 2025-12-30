package com.pismo.transactions.repository;

import com.pismo.transactions.entity.Transaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT t FROM Transaction t 
                WHERE t.account.accountId = :accountId 
                AND t.balance < 0
                AND t.operationType.operationTypeId in (1,2,3)
                ORDER BY t.transactionId ASC
            """)
    List<Transaction> findTransactionForClearingByAccountId(Long accountId);
}
