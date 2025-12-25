package com.pismo.transactions.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateTransactionRequest(

        @JsonProperty("account_id")
        @NotNull
        Long accountId,

        @JsonProperty("operation_type_id")
        @NotNull
        Integer operationTypeId,

        @NotNull
        BigDecimal amount
) {}
