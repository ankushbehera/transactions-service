package com.pismo.transactions.service;

import com.pismo.transactions.dto.request.CreateAccountRequest;
import com.pismo.transactions.dto.response.AccountResponse;
import com.pismo.transactions.entity.Account;

public interface AccountService {

    AccountResponse create(CreateAccountRequest request);

    AccountResponse get(Long accountId);

    Account getEntity(Long accountId);
}