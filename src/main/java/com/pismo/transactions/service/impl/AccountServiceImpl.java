package com.pismo.transactions.service.impl;

import com.pismo.transactions.dto.request.CreateAccountRequest;
import com.pismo.transactions.dto.response.AccountResponse;
import com.pismo.transactions.entity.Account;
import com.pismo.transactions.exception.ExceptionHelper;
import com.pismo.transactions.repository.AccountRepository;
import com.pismo.transactions.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository repository;

    @Override
    public AccountResponse create(CreateAccountRequest request) {

        LOGGER.debug("Account request: {}", request);

        Account account = Account.builder()
                .documentNumber(request.documentNumber())
                .createdAt(LocalDateTime.now())
                .build();

        try {
            Account saved = repository.saveAndFlush(account);

            return AccountResponse.builder()
                    .accountId(saved.getAccountId())
                    .documentNumber(saved.getDocumentNumber())
                    .build();

        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                LOGGER.error("Constraint violation while creating account", ex);
                throw ExceptionHelper.duplicateAccount(request.documentNumber());
            }
            LOGGER.error("Dto Exception while creating account", ex);
            throw ex;
        }
    }

    @Override
    public AccountResponse get(Long id) {

        Account acc = repository.findById(id)
                .orElseThrow(() -> ExceptionHelper.accountNotFound(id));

        return AccountResponse.builder()
                .accountId(acc.getAccountId())
                .documentNumber(acc.getDocumentNumber())
                .build();
    }

    @Override
    public Account getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ExceptionHelper.accountNotFound(id));
    }
}
