package com.tmp.tmp1cservice.controllers;

import com.tmp.tmp1cservice.dtos.ClientDTOs.ClientDto;
import com.tmp.tmp1cservice.exceptions.BadRequestException;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.services.impl.ClientServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/clients")
public class ClientsController {

    private final ClientServiceImpl service;

    public ClientsController(ClientServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public List<Client> GetClients()
    {
        return service.GetClients();
    }

    @GetMapping("/{clientId}")
    public Client GetClientId(@PathVariable UUID clientId)
    {
        return service.GetClient(clientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, UUID> RegisterClients(@RequestBody ClientDto clientDto)
    {
        return Map.of("id1c", service.RegisterClient(clientDto));
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
    public Map<String, String> testConnection(@RequestBody ClientDto dto) {
        if (service.testConnection(dto))
            return Map.of("message", "соединение установлено");
        throw new BadRequestException(String.format("Не удалось установить соединение с %s", dto.getUrl1C()));
    }
}
