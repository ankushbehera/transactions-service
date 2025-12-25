package com.pismo.transactions.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operations_types")
public class OperationType {

    @Id
    private Integer operationTypeId;

    @Column(nullable=false)
    private String description;
}
