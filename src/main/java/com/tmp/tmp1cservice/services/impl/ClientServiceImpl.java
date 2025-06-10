package com.tmp.tmp1cservice.services.impl;

import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;
import com.tmp.tmp1cservice.exceptions.BadRequestException;
import com.tmp.tmp1cservice.exceptions.ClientNotFoundException;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.services.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repo;

    public Flux<Client> GetClients() {
        log.debug("Получение списка клиентов");
        return repo.findAll();
    }

    public Mono<Client> GetClient(UUID clientId) {
        return repo.findById(clientId)
                .switchIfEmpty(((Mono.error(new ClientNotFoundException("Клиент не найден")))));
    }

    @Transactional
    public Mono<UUID> RegisterClient(ClientDto clientDto) {
        return repo.findByLogin(clientDto.getLogin())
                .flatMap(existing ->
                        Mono.error(new BadRequestException("Клиент с таким логином уже существует")))
                .then(
                        Mono.defer(() -> {
                            Client newClient = new Client(
                                    clientDto.getLogin(),
                                    clientDto.getPassword(),
                                    clientDto.getUrl1C()
                            );
                            return repo.save(newClient)
                                    .map(Client::getId)
                                    .doOnSuccess(id -> log.info("Клиент зарегистрирован с ID {}", id));
                        })
                );
    }

    @Transactional
    public Mono<Boolean> UpdateClient(ClientDto clientDto, UUID clientId) {
        return repo.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException("Клиент не найден")))
                .flatMap(client -> {
                    client.setLogin(clientDto.getLogin());
                    client.setPassword(clientDto.getPassword());
                    client.setUrl1c(clientDto.getUrl1C());
                    return repo.save(client);
                })
                .thenReturn(true);
    }

    @Transactional
    public Mono<Boolean> DeleteClient(UUID clientId) {
        return repo.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException("Клиент не найден")))
                .flatMap(client -> repo.delete(client).thenReturn(true));
    }

    public Mono<Boolean> testConnection(ClientDto dto) {
        return repo.testConnection(dto);
    }
}