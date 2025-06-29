package com.tmp.tmp1cservice.controllers;

import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;
import com.tmp.tmp1cservice.exceptions.BadRequestException;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.services.impl.ClientServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/clients")
@RequiredArgsConstructor
public class ClientsController {

    private final ClientServiceImpl service;

    @GetMapping
    public Flux<Client> GetClients()
    {
        return service.GetClients();
    }

    @GetMapping("/{clientId}")
    public Mono<Client> GetClientId(@PathVariable UUID clientId)
    {
        return service.GetClient(clientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Map<String, UUID>> RegisterClients(@RequestBody ClientDto clientDto)
    {
        return service.RegisterClient(clientDto)
                .map(id -> Map.of("id1c", id));
    }

    @PutMapping("/{clientId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void UpdateClient(@RequestBody ClientDto clientDto, @PathVariable UUID clientId)
    {
        service.UpdateClient(clientDto, clientId);
    }

    @DeleteMapping("/{clientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void DeleteClient(@PathVariable UUID clientId)
    {
        service.DeleteClient(clientId);
    }

    @GetMapping("/test")
    public Mono<Map<String, String>> testConnection(@RequestBody ClientDto dto) {
        return service.testConnection(dto)
                .flatMap(success -> {
                    if (success) {
                        return Mono.just(Map.of("message", "соединение установлено"));
                    } else {
                        return Mono.error(new BadRequestException(
                                String.format("Не удалось установить соединение с %s", dto.getUrl1C())
                        ));
                    }
                });
    }
}
