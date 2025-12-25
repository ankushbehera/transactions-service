package com.pismo.transactions.domain;

import com.pismo.transactions.exception.InvalidTransactionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperationTypeRuleTest {

    @Test
    void fromId_shouldReturnCorrectEnum() {
        assertEquals(OperationTypeRule.CASH_PURCHASE, OperationTypeRule.fromId(1));
        assertEquals(OperationTypeRule.INSTALLMENT_PURCHASE, OperationTypeRule.fromId(2));
        assertEquals(OperationTypeRule.WITHDRAWAL, OperationTypeRule.fromId(3));
        assertEquals(OperationTypeRule.PAYMENT, OperationTypeRule.fromId(4));
    }

    @Test
    void fromId_shouldThrow_WhenInvalid() {
        assertThrows(InvalidTransactionException.class,
                () -> OperationTypeRule.fromId(99));
    }

    @Test
    void payment_shouldAcceptPositive() {
        OperationTypeRule.PAYMENT.validateAmount(BigDecimal.valueOf(100));
    }

    @Test
    void payment_shouldRejectNegative() {
        assertThrows(InvalidTransactionException.class,
                () -> OperationTypeRule.PAYMENT.validateAmount(BigDecimal.valueOf(-50)));
    }

    @Test
    void payment_shouldRejectZero() {
        assertThrows(InvalidTransactionException.class,
                () -> OperationTypeRule.PAYMENT.validateAmount(BigDecimal.ZERO));
    }

    @Test
    void purchase_shouldAcceptNegative() {
        OperationTypeRule.CASH_PURCHASE.validateAmount(BigDecimal.valueOf(-100));
    }

    @Test
    void purchase_shouldRejectPositive() {
        assertThrows(InvalidTransactionException.class,
                () -> OperationTypeRule.CASH_PURCHASE.validateAmount(BigDecimal.valueOf(100)));
    }

    @Test
    void purchase_shouldRejectZero() {
        assertThrows(InvalidTransactionException.class,
                () -> OperationTypeRule.CASH_PURCHASE.validateAmount(BigDecimal.ZERO));
    }
}
