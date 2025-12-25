package com.pismo.transactions.service.impl;

import com.pismo.transactions.dto.request.CreateAccountRequest;
import com.pismo.transactions.dto.response.AccountResponse;
import com.pismo.transactions.entity.Account;
import com.pismo.transactions.exception.DuplicateAccountException;
import com.pismo.transactions.exception.ResourceNotFoundException;
import com.pismo.transactions.repository.AccountRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountServiceImpl service;

    // ---------------- CREATE ----------------

    @Test
    void create_ShouldSaveAndReturnResponse_WhenSuccess() {
        CreateAccountRequest request = new CreateAccountRequest("123456789000");

        Account saved = Account.builder()
                .accountId(10L)
                .documentNumber("123456789000")
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.saveAndFlush(any(Account.class)))
                .thenReturn(saved);

        AccountResponse response = service.create(request);

        assertEquals(10L, response.accountId());
        assertEquals("123456789000", response.documentNumber());

        verify(repository).saveAndFlush(any(Account.class));
    }

    @Test
    void create_ShouldThrowDuplicate_WhenHibernateConstraintViolationOccurs() {
        CreateAccountRequest request = new CreateAccountRequest("123456789000");

        ConstraintViolationException hibernateViolation =
                new ConstraintViolationException("duplicate", new SQLException(), "document_number");

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("duplicate", hibernateViolation);

        when(repository.saveAndFlush(any(Account.class)))
                .thenThrow(exception);

        DuplicateAccountException ex =
                assertThrows(DuplicateAccountException.class, () -> service.create(request));
        assertEquals("Account already exists for document number: 123456789000", ex.getMessage());

        verify(repository).saveAndFlush(any(Account.class));
    }

    @Test
    void create_ShouldRethrowDataIntegrityViolation_WhenOtherCause() {
        CreateAccountRequest request = new CreateAccountRequest("123456789000");

        when(repository.saveAndFlush(any(Account.class)))
                .thenThrow(new DataIntegrityViolationException("unknown"));

        DataIntegrityViolationException ex =
                assertThrows(DataIntegrityViolationException.class, () -> service.create(request));

        assertEquals("unknown", ex.getMessage());
        verify(repository).saveAndFlush(any(Account.class));
    }

    // ---------------- GET ----------------

    @Test
    void get_ShouldReturnAccount_WhenFound() {
        Account acc = Account.builder()
                .accountId(5L)
                .documentNumber("123456789000")
                .build();

        when(repository.findById(5L))
                .thenReturn(Optional.of(acc));

        AccountResponse response = service.get(5L);

        assertEquals(5L, response.accountId());
        assertEquals("123456789000", response.documentNumber());

        verify(repository).findById(5L);
    }

    @Test
    void get_ShouldThrowNotFound_WhenMissing() {
        when(repository.findById(5L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex =
                assertThrows(ResourceNotFoundException.class, () -> service.get(5L));

        assertEquals("Account with id 5 was not found", ex.getMessage());
        verify(repository).findById(5L);
    }

    // ---------------- FIND ENTITY ----------------

    @Test
    void findEntityById_ShouldReturnEntity_WhenExists() {
        Account acc = Account.builder()
                .accountId(7L)
                .documentNumber("123456789000")
                .build();

        when(repository.findById(7L))
                .thenReturn(Optional.of(acc));

        Account result = service.getEntity(7L);

        assertEquals(7L, result.getAccountId());
        assertEquals("123456789000", result.getDocumentNumber());
    }

    @Test
    void findEntityById_ShouldThrowNotFound_WhenMissing() {
        when(repository.findById(7L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex =
                assertThrows(ResourceNotFoundException.class, () -> service.getEntity(7L));

        assertEquals("Account with id 7 was not found", ex.getMessage());
    }
}
