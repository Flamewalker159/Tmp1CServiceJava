package com.tmp.tmp1cservice.repositories;

import com.tmp.tmp1cservice.models.Client;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends ReactiveCrudRepository<Client, UUID>, ClientRepositoryCustom
{
    Mono<Optional<Object>> findByLogin(String login);
}