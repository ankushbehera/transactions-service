package com.pismo.transactions.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record Error(

        @Schema(description = "Short error message", example = "BAD_REQUEST")
        String message,

        @Schema(description = "Detailed error description", example = "amount cannot be null")
        String details)
{
}