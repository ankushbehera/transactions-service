package com.pismo.transactions.domain;

import com.pismo.transactions.exception.ExceptionHelper;
import com.pismo.transactions.exception.InvalidTransactionException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
public enum OperationTypeRule {

    CASH_PURCHASE(1, false),
    INSTALLMENT_PURCHASE(2, false),
    WITHDRAWAL(3, false),
    PAYMENT(4, true);

    private final int id;
    private final boolean positiveRequired;

    OperationTypeRule(int id, boolean positiveRequired) {
        this.id = id;
        this.positiveRequired = positiveRequired;
    }

    public static OperationTypeRule fromId(int id) {
        return Arrays.stream(values())
                .filter(v -> v.id == id)
                .findFirst()
                .orElseThrow(() ->
                        ExceptionHelper.invalidOperationTypeId(id));
    }

    public void validateAmount(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw ExceptionHelper.zeroAmount();
        }

        int sign = amount.signum();

        if (positiveRequired && sign <= 0) {
            throw ExceptionHelper.positiveRequired(amount);
        }

        if (!positiveRequired && sign >= 0) {
            throw ExceptionHelper.negativeRequired(amount);
        }
    }
}
