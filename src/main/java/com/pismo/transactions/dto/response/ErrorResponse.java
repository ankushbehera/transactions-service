package com.pismo.transactions.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ErrorResponse(List<Error> errors){

    public static ErrorResponse of(String message, String details) {
        Error error = Error.builder()
                .message(message)
                .details(details)
                .build();
        return ErrorResponse.builder()
                .errors(List.of(error))
                .build();
    }

}
