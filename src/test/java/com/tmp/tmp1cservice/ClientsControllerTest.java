package com.tmp.tmp1cservice;

import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;
import com.tmp.tmp1cservice.dtos.TelematicsDTOs.TelematicsDataDto;
import com.tmp.tmp1cservice.exceptions.BadRequestException;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static javax.management.Query.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ClientRepository clientRepository;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String API_KEY = "qweasd";
    private static final String BASE_URL = "/api/clients";

    private Client testClient;
    private ClientDto testClientDto;

    @BeforeEach
    void setUp() {
        testClient = getTestClient();
        testClientDto = getTestClientDto();
    }

    private Client getTestClient(UUID id) {
        Client client = new Client();
        client.setId(id != null ? id : UUID.randomUUID());
        client.setLogin("TestClient");
        client.setPassword("123");
        client.setUrl1c("http://localhost/1CTest");
        return client;
    }

    private Client getTestClient() {
        return getTestClient(null);
    }

    private ClientDto getTestClientDto() {
        ClientDto dto = new ClientDto();
        dto.setLogin("TestClient");
        dto.setPassword("123");
        dto.setUrl1C("http://localhost/1CTest");
        return dto;
    }

    @Nested
    class GetClients {
        @Nested
        @DisplayName("return 200")
        class Return200 {
            @Test
            void shouldReturnOkWhenClientsExist() {
                // Arrange
                List<Client> clients = List.of(getTestClient(), getTestClient());
                when(clientRepository.findAll()).thenReturn(Flux.fromIterable(clients));

                // Act & Assert
                webTestClient.get()
                        .uri(BASE_URL)
                        .header(API_KEY_HEADER, API_KEY)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(Client.class)
                        .hasSize(2);

                verify(clientRepository, times(1)).findAll();
            }
        }

        @Nested
        @DisplayName("return 401")
        class Return401 {

            @Test
            void shouldReturnUnauthorizedWhenNoApiKey() {
                // Act & Assert
                webTestClient.get()
                        .uri(BASE_URL)
                        .exchange()
                        .expectStatus().isUnauthorized();
            }
        }

        @Test
        void returns401_WhenNoApiKey() {
            webTestClient.get().uri("/api/clients")
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    class GetClient {

        @Nested
        class Return200 {
            @Test
            void shouldReturnOkWhenClientExists() {
                UUID clientId = testClient.getId();
                when(clientRepository.findById(clientId)).thenReturn(Mono.just(testClient));

                webTestClient.get()
                        .uri(BASE_URL + "/{id}", clientId)
                        .header(API_KEY_HEADER, API_KEY)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(Client.class)
                        .isEqualTo(testClient);
            }
        }

        @Nested
        class Return400 {
            @Test
            void shouldReturnBadRequestWhenInvalidGuid() {
                webTestClient.get()
                        .uri(BASE_URL + "/InvalidId")
                        .header(API_KEY_HEADER, API_KEY)
                        .exchange()
                        .expectStatus().isBadRequest();
            }
        }

        @Nested
        class Return401 {
            @Test
            void shouldReturnUnauthorizedWhenNoApiKey() {
                webTestClient.get()
                        .uri(BASE_URL + "/{id}", UUID.randomUUID())
                        .exchange()
                        .expectStatus().isUnauthorized();
            }
        }

        @Nested
        class Return404 {
            @Test
            void shouldReturnNotFoundWhenClientDoesNotExist() {
                UUID clientId = UUID.randomUUID();
                when(clientRepository.findById(clientId)).thenReturn(Mono.empty());

                webTestClient.get()
                        .uri(BASE_URL + "/{id}", clientId)
                        .header(API_KEY_HEADER, API_KEY)
                        .exchange()
                        .expectStatus().isNotFound();
            }
        }
    }

    @Nested
    class RegisterClient {

        @Nested
        class Return201 {
            @Test
            void shouldReturnCreatedAndClientIdInResponse() {
                UUID clientId = UUID.randomUUID();
                Client savedClient = getTestClient(clientId);

                when(clientRepository.findByLogin(testClientDto.getLogin()))
                        .thenReturn(Mono.empty());
                when(clientRepository.save(any(Client.class)))
                        .thenReturn(Mono.just(savedClient));

                webTestClient.post()
                        .uri(BASE_URL)
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(testClientDto)
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody()
                        .jsonPath("$.id1c").isEqualTo(clientId.toString());

                verify(clientRepository).findByLogin(testClientDto.getLogin());
                verify(clientRepository).save(any(Client.class));
            }
        }

        @Nested
        @DisplayName("return 400")
        class Return400 {

            @Test
            void shouldReturnBadRequestWhenClientDtoIsInvalid(){
                // Arrange
                String invalidDto = "";

                // Act & Assert
                webTestClient.post()
                        .uri(BASE_URL)
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(invalidDto)
                        .exchange()
                        .expectStatus().isBadRequest();
            }
        }

        @Nested
        @DisplayName("return 401")
        class Return401 {

            @Test
            void shouldReturnUnauthorizedWhenNoApiKey() {
                // Act & Assert
                webTestClient.post()
                        .uri(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(testClientDto)
                        .exchange()
                        .expectStatus().isUnauthorized();
            }
        }
    }

    @Nested
    @DisplayName("UpdateClient")
    class UpdateClient {

        @Nested
        @DisplayName("return 200")
        class Return200 {

            @Test
            void shouldReturnOkWhenClientExists() {
                // Arrange
                UUID clientId = testClient.getId();
                when(clientRepository.findById(clientId))
                        .thenReturn(Mono.just(testClient));
                when(clientRepository.save(any(ClientDto.class))
                        .thenReturn(Mono.just());

                // Act & Assert
                webTestClient.put()
                        .uri(BASE_URL + "/{id}", clientId)
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(testClientDto)
                        .exchange()
                        .expectStatus().isOk();
                verify(clientRepository).findByLogin(testClientDto.getLogin());
                verify(clientRepository).save(any(Client.class));
            }
        }

        @Nested
        @DisplayName("return 400")
        class Return400 {

            @Test
            void shouldReturnBadRequestWhenInvalidGuid()  {
                // Act & Assert
                webTestClient.put()
                        .uri(BASE_URL + "/invalid-guid")
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(testClientDto)
                        .exchange()
                        .expectStatus().isBadRequest();
            }

            @Test
            void shouldReturnBadRequestWhenClientDtoIsNull() {
                // Act & Assert
                webTestClient.put()
                        .uri(BASE_URL + "/{id}", UUID.randomUUID())
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue((ClientDto) null)
                        .exchange()
                        .expectStatus().isBadRequest();
            }

            @Test
            void shouldReturnBadRequestWhenGuidEmpty() {
                // Act & Assert
                webTestClient.put()
                        .uri(BASE_URL + "/{id}", UUID.fromString("00000000-0000-0000-0000-000000000000"))
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue((ClientDto) null)
                        .exchange()
                        .expectStatus().isBadRequest();
            }
        }

        @Nested
        @DisplayName("return 401")
        class Return401 {

            @Test
            void shouldReturnUnauthorizedWhenNoApiKey() {
                // Act & Assert
                webTestClient.put()
                        .uri(BASE_URL + "/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(testClientDto)
                        .exchange()
                        .expectStatus().isUnauthorized();
            }
        }

        @Nested
        @DisplayName("return 404")
        class Return404 {

            @Test
            void shouldReturnNotFoundWhenClientNotFound()  {
                // Arrange
                UUID clientId = UUID.randomUUID();
                when(clientRepository.findById(clientId)).thenReturn(Mono.empty());

                // Act & Assert
                webTestClient.put()
                        .uri(BASE_URL + "/{id}", clientId)
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(testClientDto)
                        .exchange()
                        .expectStatus().isNotFound();
            }
        }
    }

    @Nested
    @DisplayName("DeleteClient")
    class DeleteClient {

        @Nested
        @DisplayName("return 204")
        class Return204 {

            @Test
            void shouldReturnNoContentWhenClientExists() {
                // Arrange
                UUID clientId = testClient.getId();
                when(clientRepository.findById(clientId)).thenReturn(Mono.just(testClient));
                when(clientRepository.deleteById(clientId)).thenReturn(Mono.empty());

                // Act & Assert
                webTestClient.delete()
                        .uri(BASE_URL + "/{id}", clientId)
                        .header(API_KEY_HEADER, API_KEY)
                        .exchange()
                        .expectStatus().isNoContent();
            }
        }

        @Nested
        @DisplayName("return 401")
        class Return401 {

            @Test
            void shouldReturnUnauthorizedWhenNoApiKey() {
                // Act & Assert
                webTestClient.delete()
                        .uri(BASE_URL + "/{id}", UUID.randomUUID())
                        .exchange()
                        .expectStatus().isUnauthorized();
            }
        }

        @Nested
        @DisplayName("return 404")
        class Return404 {

            @Test
            void shouldReturnNotFoundWhenClientNotFound() {
                // Arrange
                UUID clientId = UUID.randomUUID();
                when(clientRepository.findById(clientId)).thenReturn(Mono.empty());

                // Act & Assert
                webTestClient.delete()
                        .uri(BASE_URL + "/{id}", clientId)
                        .header(API_KEY_HEADER, API_KEY)
                        .exchange()
                        .expectStatus().isNotFound();
            }
        }
    }

    @Nested
    @DisplayName("TestConnections")
    class TestConnections {

        @Nested
        @DisplayName("return 200")
        class Return200 {

            @Test
            void shouldReturnOkWhenConnectionIsSuccessful()  {
                // Arrange
                when(clientRepository.testConnection(any(ClientDto.class))).thenReturn(Mono.just(true));

                // Act & Assert
                webTestClient.post()
                        .uri(BASE_URL + "/test")
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(testClientDto)
                        .exchange()
                        .expectStatus().isOk();
            }
        }

        @Nested
        @DisplayName("return 400")
        class Return400 {

            @Test
            void shouldReturnBadRequestWhenUrlIsInvalid()  {
                // Arrange
                ClientDto invalidDto = new ClientDto();
                invalidDto.setLogin("Web1");
                invalidDto.setPassword("123");
                invalidDto.setUrl1c("invalid-url");

                // Act & Assert
                webTestClient.post()
                        .uri(BASE_URL + "/test")
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(invalidDto)
                        .exchange()
                        .expectStatus().isBadRequest();
            }

            @Test
            void shouldReturnBadRequestWhenConnectionFails()  {
                // Arrange
                when(clientRepository.testConnection(any(ClientDto.class))).thenReturn(Mono.just(false));

                // Act & Assert
                webTestClient.post()
                        .uri(BASE_URL + "/test")
                        .header(API_KEY_HEADER, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(testClientDto)
                        .exchange()
                        .expectStatus().isBadRequest();
            }
        }

        @Nested
        @DisplayName("return 401")
        class Return401 {

            @Test
            void shouldReturnUnauthorizedWhenNoApiKey()  {
                // Arrange
                ClientDto invalidDto = new ClientDto();
                invalidDto.setLogin("Web1");
                invalidDto.setPassword("123");
                invalidDto.setUrl1c("invalid-url");

                // Act & Assert
                webTestClient.post()
                        .uri(BASE_URL + "/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(invalidDto)
                        .exchange()
                        .expectStatus().isUnauthorized();
            }
        }
    }
}

