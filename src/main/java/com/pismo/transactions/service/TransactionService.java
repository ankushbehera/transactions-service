package com.pismo.transactions.service;

import com.pismo.transactions.dto.request.CreateTransactionRequest;
import com.pismo.transactions.dto.response.TransactionResponse;

public interface TransactionService {
    TransactionResponse create(CreateTransactionRequest request);
}