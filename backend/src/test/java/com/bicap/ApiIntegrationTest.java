package com.bicap;

import com.bicap.blockchain.BlockchainClient;
import com.bicap.common.enums.RoleName;
import com.bicap.entity.Role;
import com.bicap.repository.RoleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end HTTP integration tests backed by an isolated PostgreSQL container.
 * Docker is required; Testcontainers skips this class when Docker is unavailable.
 */
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class ApiIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Blockchain is outside the authentication flow and must not make RPC calls
     * while this HTTP/database integration test runs.
     */
    @MockitoBean
    private BlockchainClient blockchainClient;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }

    @BeforeEach
    void ensureRetailerRoleExists() {
        roleRepository.findByRoleName(RoleName.RETAILER.name())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName(RoleName.RETAILER.name())
                        .build()));
    }

    @Test
    void registerLoginAndGetCurrentUser_shouldWorkEndToEnd() throws Exception {
        String username = "retailer_" + UUID.randomUUID().toString().replace("-", "");
        String password = "password123";
        String email = username + "@example.com";

        String registerRequest = """
                {
                  "username":"%s",
                  "password":"%s",
                  "fullName":"Integration Retailer",
                  "email":"%s",
                  "phone":"0900000000",
                  "retailerName":"Integration Store",
                  "address":"Ho Chi Minh City"
                }
                """.formatted(username, password, email);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value(RoleName.RETAILER.name()))
                .andReturn();

        JsonNode loginBody = objectMapper.readTree(
                loginResult.getResponse().getContentAsString());
        String accessToken = loginBody.path("accessToken").asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value(RoleName.RETAILER.name()));
    }
}
