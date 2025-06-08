package com.tmp.tmp1cservice.services;

import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;
import com.tmp.tmp1cservice.models.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService
{
    List<Client> GetClients();
    Client GetClient(UUID clientId);
    UUID RegisterClient(ClientDto clientDto);
    boolean testConnection(ClientDto clientDto);
    boolean UpdateClient(ClientDto clientDto, UUID clientId);
    boolean DeleteClient(UUID clientId);
}