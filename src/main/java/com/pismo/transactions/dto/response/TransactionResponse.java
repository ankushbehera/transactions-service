package com.pismo.transactions.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionResponse(

        @JsonProperty("transaction_id")
        Long transactionId,

        @JsonProperty("account_id")
        Long accountId,

        @JsonProperty("operation_type_id")
        Integer operationTypeId,

        BigDecimal amount,

        BigDecimal balance,

        @JsonProperty("event_date")
        LocalDateTime eventDate
) {}
