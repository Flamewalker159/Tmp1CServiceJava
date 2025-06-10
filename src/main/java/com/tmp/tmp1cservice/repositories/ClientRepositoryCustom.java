package com.tmp.tmp1cservice.repositories;

import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;
import reactor.core.publisher.Mono;

public interface ClientRepositoryCustom {
    Mono<Boolean> testConnection(ClientDto dto);
}
