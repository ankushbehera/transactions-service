package com.pismo.transactions.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.transactions.dto.request.CreateTransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class TransactionControllerIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;


    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(1L, 4, BigDecimal.valueOf(100))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction_id").exists())
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.operation_type_id").value(4))
                .andExpect(jsonPath("$.event_date").exists());
    }

    @Test
    void shouldReturnBadRequestForInvalidAmount() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(1L, 1, BigDecimal.valueOf(100))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].message").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].details").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequest_WhenJsonInvalid() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"amount\": \"invalid\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message").value("BAD_REQUEST"));
    }

    private String request(Long accId, int opId, BigDecimal amount) throws JsonProcessingException {
        CreateTransactionRequest request =
                CreateTransactionRequest.builder()
                        .accountId(accId)
                        .operationTypeId(opId)
                        .amount(amount)
                        .build();
        return mapper.writeValueAsString(request);
    }
}
