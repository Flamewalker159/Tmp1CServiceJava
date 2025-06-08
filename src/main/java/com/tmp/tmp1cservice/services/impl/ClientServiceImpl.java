package com.tmp.tmp1cservice.services.impl;

import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;
import com.tmp.tmp1cservice.exceptions.BadRequestException;
import com.tmp.tmp1cservice.exceptions.ClientNotFoundException;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.services.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repo;

    public ClientServiceImpl(ClientRepository repo) {
        this.repo = repo;
    }

    public List<Client> GetClients() {
        log.debug("Получение списка клиентов");
        var clients = repo.findAll();
        log.info("Получено {} клиентов", clients.size());
        return clients;
    }

    public Client GetClient(UUID clientId) {
        var client = repo.findById(clientId).orElseThrow(() -> {
            log.error("Клиент {} не найден", clientId);
            return new ClientNotFoundException(String.format("Клиент с ID %s не найден.", clientId));
        });

        log.info("Клиент {} успешно получен", clientId);
        return client;
    }

    @Transactional
    public UUID RegisterClient(ClientDto clientDto) {
        log.debug("Регистрация клиента с логином {}", clientDto.getLogin());
        if (repo.findByLogin(clientDto.getLogin()).isPresent())
            throw new BadRequestException(String.format("Клиент с логином %s уже существует.", clientDto.getLogin()));
        Client client = new Client(clientDto.getLogin(), clientDto.getPassword(), clientDto.getUrl1C());
        var result = repo.save(client).getId();
        log.info("Клиент с логином {} зарегистрирован с ID {}", clientDto.getLogin(), client.getId());
        return result;
    }

    @Transactional
    public boolean UpdateClient(ClientDto clientDto, UUID clientId) {
        log.debug("Обновление клиента {ClientId}", clientId);
        var client = repo.findById(clientId).orElseThrow(() -> {
            log.error("Клиент {} не найден", clientId);
            throw new ClientNotFoundException(String.format("Клиент с ID %s не найден.", clientId));
        });

        client.setLogin(clientDto.getLogin());
        client.setPassword(clientDto.getPassword());
        client.setUrl1c(clientDto.getUrl1C());
        repo.save(client);
        log.info("Данные клиента {} успешно обновлены", clientId);
        return true;
    }

    @Transactional
    public boolean DeleteClient(UUID clientId) {
        log.debug("Удаление клиента {}", clientId);
        var client = repo.findById(clientId).orElseThrow(() -> {
            log.error("Клиент {} не найден в БД", clientId);
            throw new ClientNotFoundException(String.format("Клиент с ID %s не найден в базе данных.", clientId));
        });
        repo.delete(client);
        log.info("Клиент {} успешно удален", clientId);
        return true;
    }

    public boolean testConnection(ClientDto dto) {
        return repo.testConnection(dto);
    }
}