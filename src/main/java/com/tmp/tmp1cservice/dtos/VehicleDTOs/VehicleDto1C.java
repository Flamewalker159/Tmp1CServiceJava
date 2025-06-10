package com.tmp.tmp1cservice.dtos.VehicleDTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tmp.tmp1cservice.utils.OffsetDateTimeTo1CSerializer;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class VehicleDto1C {
    @JsonProperty("Код")    // для сериализации
    @JsonAlias("code1C")    // для десериализации
    private String code1C;

    @JsonProperty("Наименование")
    @JsonAlias("name")
    private String name;

    @JsonProperty("ГосНомер")
    @JsonAlias("licensePlate")
    private String licensePlate;

    @JsonProperty("VIN")
    @JsonAlias("vin")
    private String vin;

    @JsonProperty("Марка")
    @JsonAlias("brand")
    private String brand;

    @JsonProperty("Модель")
    @JsonAlias("model")
    private String model;

    @JsonProperty("ГруппаТС")
    @JsonAlias("groupId")
    private String groupId;

    @JsonProperty("ГодВыпуска")
    @JsonAlias("yearOfIssue")
    @JsonSerialize(using = OffsetDateTimeTo1CSerializer.class)
    private OffsetDateTime yearOfIssue;

    @JsonProperty("Масса")
    @JsonAlias("mass")
    private Integer mass;

    @JsonProperty("Габариты")
    @JsonAlias("dimensions")
    private String dimensions;

    @JsonProperty("ТипВладенияТС")
    @JsonAlias("ownershipType")
    private String ownershipType;

    @JsonProperty("НомерШасси")
    @JsonAlias("chassisNumber")
    private String chassisNumber;

    @JsonProperty("НомерДвигателя")
    @JsonAlias("engineNumber")
    private String engineNumber;

    @JsonProperty("МодельДвигателя")
    @JsonAlias("engineModel")
    private String engineModel;

    @JsonProperty("МощностьДвигателяЛС")
    @JsonAlias("powerEngineInLs")
    private Integer powerEngineInLs;

    @JsonProperty("МощностьДвигателяКвт")
    @JsonAlias("powerEngineInKvt")
    private Integer powerEngineInKvt;
}