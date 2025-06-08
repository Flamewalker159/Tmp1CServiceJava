package com.tmp.tmp1cservice.repositories;

import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;

public interface ClientRepositoryCustom {
    boolean testConnection(ClientDto dto);
}
