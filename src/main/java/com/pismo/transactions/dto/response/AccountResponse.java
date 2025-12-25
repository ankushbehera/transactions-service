package com.pismo.transactions.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AccountResponse(

        @JsonProperty("account_id")
        Long accountId,

        @JsonProperty("document_number")
        String documentNumber
) {}
