package com.tmp.tmp1cservice.dtos.VehicleDTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class VehicleDto {
    @JsonAlias("Код")               // для десериализации ищем ключ "Код"
    @JsonProperty("code1C")         // для сериализации используем "code1C"
    private String code1C;

    @JsonAlias("Наименование")
    @JsonProperty("name")
    private String name;

    @JsonAlias("ГосНомер")
    @JsonProperty("licensePlate")
    private String licensePlate;

    @JsonAlias("VIN")
    @JsonProperty("vin")
    private String vin;

    @JsonAlias("Марка")
    @JsonProperty("brand")
    private String brand;

    @JsonAlias("Модель")
    @JsonProperty("model")
    private String model;

    @JsonAlias("ГруппаТС")
    @JsonProperty("groupId")
    private String groupId;

    @JsonAlias("ГодВыпуска")
    @JsonProperty("yearOfIssue")
    private LocalDate yearOfIssue;

    @JsonAlias("Масса")
    @JsonProperty("mass")
    private Integer mass;

    @JsonAlias("Габариты")
    @JsonProperty("dimensions")
    private String dimensions;

    @JsonAlias("ТипВладенияТС")
    @JsonProperty("ownershipType")
    private String ownershipType;

    @JsonAlias("НомерШасси")
    @JsonProperty("chassisNumber")
    private String chassisNumber;

    @JsonAlias("НомерДвигателя")
    @JsonProperty("engineNumber")
    private String engineNumber;

    @JsonAlias("МодельДвигателя")
    @JsonProperty("engineModel")
    private String engineModel;

    @JsonAlias("МощностьДвигателяЛС")
    @JsonProperty("powerEngineInLs")
    private Integer powerEngineInLs;

    @JsonAlias("МощностьДвигателяКвт")
    @JsonProperty("powerEngineInKvt")
    private Integer powerEngineInKvt;
}