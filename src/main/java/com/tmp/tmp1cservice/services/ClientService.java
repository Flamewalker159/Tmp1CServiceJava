package com.tmp.tmp1cservice.services;

import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;
import com.tmp.tmp1cservice.models.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClientService
{
    Flux<Client> GetClients();
    Mono<Client> GetClient(UUID clientId);
    Mono<UUID> RegisterClient(ClientDto clientDto);
    Mono<Boolean> testConnection(ClientDto clientDto);
    Mono<Boolean> UpdateClient(ClientDto clientDto, UUID clientId);
    Mono<Boolean> DeleteClient(UUID clientId);
}