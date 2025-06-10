package com.tmp.tmp1cservice.services.impl.OData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmp.tmp1cservice.dtos.TelematicsDTOs.OData.TelematicsDataDtoOData;
import com.tmp.tmp1cservice.exceptions.ClientNotFoundException;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.repositories.OData.TelematicsRepositoryOData;
import com.tmp.tmp1cservice.services.OData.TelematicsServiceOData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelematicsServiceODataImpl implements TelematicsServiceOData
{
    private final ClientRepository clientRepository;
    private final TelematicsRepositoryOData telematicsRepositoryOData;
    private final ObjectMapper objectMapper;

    public Mono<TelematicsDataDtoOData> sendingTelematicsData(UUID clientId, TelematicsDataDtoOData telematicsDataDto)
    {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException(
                        String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> telematicsRepositoryOData.SendingTelematicsData(client, telematicsDataDto)
                        .flatMap(response -> {
                            String body = response.getBody();
                            if (body == null || body.isBlank()) {
                                return Mono.error(new OneCRequestException(500,
                                        String.format("Некорректный ответ от 1С при создании ТС для клиента %s", clientId)));
                            }
                            try {
                                TelematicsDataDtoOData telematics = objectMapper.readValue(body, TelematicsDataDtoOData.class);
                                if (telematics == null) {
                                    return Mono.error(new OneCRequestException(400,
                                            String.format("Некорректный ответ от 1С для клиента %s", clientId)));
                                }
                                return Mono.just(telematics);
                            } catch (Exception e) {
                                return Mono.error(new OneCRequestException(500,
                                        "Ошибка при десериализации ответа от 1С: " + e.getMessage()));
                            }
                        }));
    }
}