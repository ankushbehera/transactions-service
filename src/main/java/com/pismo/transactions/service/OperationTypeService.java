package com.pismo.transactions.service;

import com.pismo.transactions.entity.OperationType;

public interface OperationTypeService {
    OperationType findEntityById(Integer id);
}
