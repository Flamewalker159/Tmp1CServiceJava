package com.tmp.tmp1cservice.repositories;

import com.tmp.tmp1cservice.models.Client;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClientRepository extends ReactiveCrudRepository<Client, UUID>, ClientRepositoryCustom
{
    Mono<Client> findByLogin(String login);
}