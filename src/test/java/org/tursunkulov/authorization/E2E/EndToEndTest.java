package org.tursunkulov.authorization.E2E;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.tursunkulov.authorization.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EndToEndTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Тест приложения")
    public void EndToEnd() {
        User firstUser = new User("Andrew", "1234", "wssw@gmail.com", "87776632233");
        User secondUser = new User("Andrey", "123wr", "swws@gmail.com", "87776632255");
        ResponseEntity<String> createUserResponse1 =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/login/registration", firstUser, String.class);
        ResponseEntity<String> createUserResponse2 =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/login/registration", secondUser, String.class);
        assertEquals(HttpStatus.OK, createUserResponse1.getStatusCode());
        assertEquals(HttpStatus.OK, createUserResponse2.getStatusCode());
        assertEquals("Success", createUserResponse1.getBody());
        assertEquals("Success", createUserResponse2.getBody());

        ResponseEntity<String> getUser1DataResponse =
                restTemplate.getForEntity("http://localhost:" + port + "/user/username/3", String.class);
        assertEquals(HttpStatus.OK, getUser1DataResponse.getStatusCode());
        assertEquals(firstUser.getUsername(), getUser1DataResponse.getBody());

        ResponseEntity<String> getUser2DataResponse =
                restTemplate.getForEntity("http://localhost:" + port + "/user/username/4", String.class);
        assertEquals(HttpStatus.OK, getUser2DataResponse.getStatusCode());
        assertEquals(secondUser.getUsername(), getUser1DataResponse.getBody());

        ResponseEntity<String> authorizationOfFirstUser =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/authorization", firstUser, String.class);
        assertEquals(HttpStatus.OK, authorizationOfFirstUser.getStatusCode());
        assertEquals("Добро пожаловать!", authorizationOfFirstUser.getBody());

        ResponseEntity<String> authorizationOfSecondUser =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/authorization", secondUser, String.class);
        assertEquals(HttpStatus.OK, authorizationOfSecondUser.getStatusCode());
        assertEquals("Добро пожаловать!", authorizationOfSecondUser.getBody());
    }
}
