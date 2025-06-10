package com.tmp.tmp1cservice.repositories.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleDto1C;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleUpdateDto;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.models.Client;
import com.tmp.tmp1cservice.repositories.VehicleRepository;
import com.tmp.tmp1cservice.utils.UrlHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class VehicleRepositoryImpl implements VehicleRepository {
    @Override
    public Mono<ResponseEntity<String>> getVehiclesFrom1C(Client client) {
        String url = UrlHelper.vehiclesUrl(client.getUrl1c());

        return UrlHelper.getWebClient(client)
                .get()
                .uri(url)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new OneCRequestException(
                                        response.statusCode().value()
                                        , String.format(
                                        "Ошибка при получении списка ТС для клиента %s: %s", client.getId(), body))))
                )
                .toEntity(String.class);
    }

    public Mono<ResponseEntity<String>> getVehicleFrom1C(Client client, String vehicleCode1C) {
        String url = UrlHelper.vehicleUrl(client.getUrl1c(), vehicleCode1C);

        return UrlHelper.getWebClient(client)
                .get()
                .uri(url)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new OneCRequestException(
                                        response.statusCode().value()
                                        , String.format(
                                        "Ошибка при получении ТС %s для клиента %s: %s", vehicleCode1C, client.getId(), body))))
                )
                .toEntity(String.class);
    }

    public Mono<ResponseEntity<String>> createVehicleIn1C(Client client, VehicleDto1C vehicleDto1C) {
        String url = UrlHelper.vehiclesUrl(client.getUrl1c());

        return UrlHelper.getWebClient(client)
                .post()
                .uri(url)
                .bodyValue(vehicleDto1C)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new OneCRequestException(
                                        response.statusCode().value(),
                                        String.format("Ошибка при создании ТС для клиента %s: %s", client.getId(), body)))))
                .toEntity(String.class);
    }

    public Mono<ResponseEntity<String>> updateVehicleIn1C(Client client, String vehicleCode1C,
                                                          VehicleUpdateDto updateDto) {
        String url = UrlHelper.vehicleUrl(client.getUrl1c(), vehicleCode1C);

        return UrlHelper.getWebClient(client)
                .put()
                .uri(url)
                .bodyValue(updateDto)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new OneCRequestException(
                                        response.statusCode().value(),
                                        String.format("Ошибка при обновлении ТС %s для клиента %s: %s", vehicleCode1C, client.getId(), body)))))
                .toEntity(String.class);
    }
}
