package org.tursunkulov.authorization.withconteiner;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.tursunkulov.Application;
import org.tursunkulov.authorization.security.SecurityConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(classes = {Application.class, SecurityConfig.class})
@ActiveProfiles("test-with-container")
@Testcontainers
public class ApplicationWithContainerTest {

    @Container
    public static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("postgres")
                    .withUsername("postgres")
                    .withPassword("JavaMTS")
                    .withInitScript("init.sql");

    @BeforeAll
    static void setUp() {
        log.info(
                "PostgreSQL контейнер хост: {}", postgres.getHost() + ":" + postgres.getFirstMappedPort());
        log.info("PostgreSQL URL соединения: {}", postgres.getJdbcUrl());
    }

    @Test
    public void testDatabaseConnection() {
        assertTrue(postgres.isRunning());
    }
}
