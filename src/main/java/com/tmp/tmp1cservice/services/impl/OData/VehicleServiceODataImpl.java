import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.OData.VehicleDtoOData;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.VehicleDto;
import com.tmp.tmp1cservice.exceptions.ClientNotFoundException;
import com.tmp.tmp1cservice.exceptions.OneCRequestException;
import com.tmp.tmp1cservice.repositories.ClientRepository;
import com.tmp.tmp1cservice.repositories.OData.VehicleRepositoryOData;
import com.tmp.tmp1cservice.repositories.VehicleRepository;
import com.tmp.tmp1cservice.services.OData.VehiclesServiceOData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleServiceODataImpl implements VehiclesServiceOData
{
    private final VehicleRepositoryOData vehicleRepositoryOData;
    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public Mono<List<VehicleDtoOData>> getVehiclesFrom1C(UUID clientId)
    {
        return Mono.justOrEmpty(clientRepository.findById(clientId))
                .switchIfEmpty(Mono.error(new ClientNotFoundException(String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepositoryOData.getVehiclesFrom1C(client)
                        .flatMap(response -> {
                            String body = response.getBody();
                            try {
                                List<VehicleDtoOData> vehicles = objectMapper
                                        .readValue(body, new TypeReference<>() {
                                        });
                                if (vehicles == null || vehicles.isEmpty()) {
                                    return Mono.error(new OneCRequestException(400,
                                            "Некорректный ответ от 1С при получении списка ТС"));
                                }
                                return Mono.just(vehicles);
                            } catch (Exception ex) {
                                return Mono.error(new OneCRequestException(400,
                                        "Ошибка при обработке ответа от 1С: " + ex.getMessage()));
                            }
                        }));
    }

    public Mono<VehicleDtoOData> getVehicleFrom1C(UUID clientId, UUID refKey)
    {
        return Mono.justOrEmpty(clientRepository.findById(clientId))
                .switchIfEmpty(Mono.error(new ClientNotFoundException(String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepositoryOData.getVehicleFrom1C(client, refKey)
                        .flatMap(stringResponseEntity -> {
                            String body = stringResponseEntity.getBody();
                            try {
                                VehicleDtoOData vehicle = objectMapper.readValue(body, new TypeReference<>() {});
                                if (vehicle == null) {
                                    return Mono.error(new OneCRequestException(400,
                                            "Некорректный ответ от 1С при получении ТС"));
                                }
                                return Mono.just(vehicle);
                            } catch (Exception ex) {
                                return Mono.error(new OneCRequestException(400,
                                        "Ошибка при обработке ответа от 1С: " + ex.getMessage()));
                            }
                        }));
    }

    public Mono<VehicleDtoOData> createVehicleIn1C(UUID clientId, VehicleDtoOData vehicleDtoOData)
    {
        return Mono.justOrEmpty(clientRepository.findById(clientId))
                .switchIfEmpty(Mono.error(new ClientNotFoundException(
                        String.format("Клиент с ID %s не найден.", clientId))))
                .flatMap(client -> vehicleRepositoryOData.createVehicleIn1C(client, vehicleDtoOData)
                        .flatMap(response -> {
                            String body = response.getBody();
                            if (body == null || body.isBlank()) {
                                return Mono.error(new OneCRequestException(400,
                                        String.format("Некорректный ответ от 1С при создании ТС для клиента %s", clientId)));
                            }
                            try {
                                VehicleDto vehicle = objectMapper.readValue(body, VehicleDto.class);
                                if (vehicle == null) {
                                    return Mono.error(new OneCRequestException(400,
                                            String.format("Некорректный ответ от 1С при создании ТС для клиента %s", clientId)));
                                }
                                return Mono.just(vehicle);
                            } catch (Exception e) {
                                return Mono.error(new OneCRequestException(400,
                                        "Ошибка при десериализации ответа от 1С: " + e.getMessage()));
                            }
                        }));
        logger.LogDebug("Создание ТС для клиента {ClientId}", clientId);
        var client = await clientRepository.GetClient(clientId);
        if (client == null)
        {
            logger.LogError("Клиент {ClientId} не найден", clientId);
            throw new ClientNotFoundException($"Клиент с ID {clientId} не найден.");
        }

        var createdVehicle = await vehicleRepository.CreateVehicleIn1C(client, vehicleDtoOData);

        if (!createdVehicle.IsSuccessStatusCode)
        {
            var errorDetails = await createdVehicle.Content.ReadAsStringAsync();
            logger.LogError("Ошибка при создании ТС для клиента {ClientId}: {StatusCode}, {Details}", clientId,
                createdVehicle.StatusCode, errorDetails);
            throw new OneCRequestException((int)createdVehicle.StatusCode,
                $"Ошибка возникшая в 1С. Код ответа: {createdVehicle.StatusCode}, детали: {errorDetails}");
        }

        var responseData = await createdVehicle.Content.ReadAsStringAsync();
        if (string.IsNullOrEmpty(responseData))
        {
            logger.LogError("Данные ТС для клиента {ClientId} не получены из 1С", clientId);
            throw new OneCRequestException(500, $"Некорректный ответ от 1С при создании ТС для клиента {clientId}.");
        }

        var vehicle = JsonConvert.DeserializeObject<ODataVehicleDto>(responseData);
        if (vehicle == null)
        {
            logger.LogError("Некорректный ответ от 1С при создании ТС для клиента {ClientId}", clientId);
            throw new OneCRequestException(500, $"Некорректный ответ от 1С при создании ТС для клиента {clientId}.");
        }

        logger.LogInformation("ТС успешно создано для клиента {ClientId}", clientId);
        return vehicle;
    }

    public Mono<VehicleDtoOData> updateVehicle1C(Guid clientId, Guid refKey,
        VehicleUpdateDto updateDto)
    {
        logger.LogDebug("Обновление ТС {VehicleCode1C} для клиента {ClientId}", refKey, clientId);
        var client = await clientRepository.GetClient(clientId);
        if (client == null)
        {
            logger.LogError("Клиент {ClientId} не найден", clientId);
            throw new ClientNotFoundException($"Клиент с ID {clientId} не найден.");
        }

        var response = await vehicleRepository.UpdateVehicleIn1C(client, refKey, updateDto);
        logger.LogInformation("ТС {VehicleCode1C} для клиента {ClientId} отправлено на обновление", refKey,
            clientId);

        if (!response.IsSuccessStatusCode)
        {
            var errorDetails = await response.Content.ReadAsStringAsync();
            logger.LogError("Ошибка при обновлении ТС с кодом {VehicleCode1C} для клиента {ClientId}: {Details}",
                refKey, clientId, errorDetails);
            throw new OneCRequestException((int)response.StatusCode,
                $"Ошибка при обновлении данных ТС: {errorDetails}");
        }

        var responseData = await response.Content.ReadAsStringAsync();
        var vehicle = JsonConvert.DeserializeObject<ODataVehicleDto>(responseData);
        return vehicle!;
    }
}