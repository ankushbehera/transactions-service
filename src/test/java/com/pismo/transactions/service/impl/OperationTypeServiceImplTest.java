package com.pismo.transactions.service.impl;

import com.pismo.transactions.entity.OperationType;
import com.pismo.transactions.exception.ResourceNotFoundException;
import com.pismo.transactions.repository.OperationTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationTypeServiceImplTest {

    @Mock
    private OperationTypeRepository repository;

    @InjectMocks
    private OperationTypeServiceImpl service;


    @Test
    void findEntityById_ShouldReturnOperationType_WhenExists() {
        OperationType op = new OperationType();
        op.setOperationTypeId(1);
        op.setDescription("PURCHASE");

        when(repository.findById(1))
                .thenReturn(Optional.of(op));

        OperationType result = service.findEntityById(1);

        assertNotNull(result);
        assertEquals(1, result.getOperationTypeId());
        assertEquals("PURCHASE", result.getDescription());
        verify(repository).findById(1);
    }

    @Test
    void findEntityById_ShouldThrowResourceNotFound_WhenNotFound() {
        when(repository.findById(2))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex =
                assertThrows(ResourceNotFoundException.class, () ->
                        service.findEntityById(2));

        assertEquals("Operation with id 2 was not found", ex.getMessage());
        verify(repository).findById(2);
    }
}
