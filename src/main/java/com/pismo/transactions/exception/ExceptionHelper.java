package com.pismo.transactions.exception;

import java.math.BigDecimal;
import java.text.MessageFormat;

public final class ExceptionHelper {

    private ExceptionHelper() {}

    public static DuplicateAccountException duplicateAccount(String documentNumber) {
        return new DuplicateAccountException(
                MessageFormat.format(
                        "Account already exists for document number: {0}",
                        documentNumber
                )
        );
    }

    public static ResourceNotFoundException accountNotFound(Long id) {
        return new ResourceNotFoundException(
                MessageFormat.format(
                        "Account with id {0} was not found",
                        id
                )
        );
    }



    public static ResourceNotFoundException OperationTypeNotFound(int id) {
        return new ResourceNotFoundException(
                MessageFormat.format("Operation with id {0} was not found", id)
        );
    }

    public static InvalidTransactionException zeroAmount() {
        return new InvalidTransactionException("Transaction amount cannot be zero");
    }

    public static InvalidTransactionException positiveRequired(BigDecimal amount) {
        return new InvalidTransactionException(
                MessageFormat.format("Payment amount must be positive. Provided: {0}", amount)
        );
    }

    public static InvalidTransactionException negativeRequired(BigDecimal amount) {
        return new InvalidTransactionException(
                MessageFormat.format(
                        "Cash / Installment / Withdrawal amount must be negative. Provided: {0}",
                        amount
                )
        );
    }

    public static InvalidTransactionException invalidOperationTypeId(int id) {
        return new InvalidTransactionException(
                MessageFormat.format("Invalid operation type id: {0}", id)
        );
    }

}
