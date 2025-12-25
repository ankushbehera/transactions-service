package com.pismo.transactions.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.transactions.dto.request.CreateAccountRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class AccountControllerIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;


    @Test
    void shouldCreateAccountSuccessfully() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("12345678912")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id").exists())
                .andExpect(jsonPath("$.document_number").value("12345678912"));
    }

    @Test
    void shouldReturnBadRequestForDuplicateAccountDocument() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("12345678900")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].message").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].details").isNotEmpty());
    }


    @Test
    void shouldGetAccountSuccessfully() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.document_number").exists());
    }
    @Test
    void shouldReturnNoAccountFound() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].message").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].details").isNotEmpty());
    }

    @Test
    void shouldReturnValidationError_WhenRequestInvalid() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors[0].message").value("VALIDATION_ERROR"));
    }


    private String request(String documentNumber) throws JsonProcessingException {
        CreateAccountRequest request =
                CreateAccountRequest.builder()
                        .documentNumber(documentNumber)
                        .build();
        return mapper.writeValueAsString(request);
    }
}
