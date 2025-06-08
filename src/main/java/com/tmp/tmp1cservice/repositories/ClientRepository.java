package com.tmp.tmp1cservice.repositories;

import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;
import com.tmp.tmp1cservice.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID>, ClientRepositoryCustom
{
    Optional<Object> findByLogin(String login);
}