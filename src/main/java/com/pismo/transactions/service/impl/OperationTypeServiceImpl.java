package com.pismo.transactions.service.impl;

import com.pismo.transactions.entity.OperationType;
import com.pismo.transactions.exception.ExceptionHelper;
import com.pismo.transactions.repository.OperationTypeRepository;
import com.pismo.transactions.service.OperationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationTypeServiceImpl implements OperationTypeService {

    private final OperationTypeRepository repository;

    @Override
    public OperationType findEntityById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> ExceptionHelper.OperationTypeNotFound(id));
    }
}
