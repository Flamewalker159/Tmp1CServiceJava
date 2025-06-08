package com.tmp.tmp1cservice.dtos.VehicleDTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VehicleUpdateDto
{
    @JsonProperty("ГосНомер")
    @JsonAlias("licensePlate")
    private String LicensePlate;

    @JsonProperty("ТипВладенияТС")
    @JsonAlias("ownershipType")
    private String OwnershipType;

    @JsonProperty("Масса")
    @JsonAlias("mass")
    private int Mass;

    @JsonProperty("Габариты")
    @JsonAlias("dimensions")
    private String Dimensions;

    @JsonProperty("НомерШасси")
    @JsonAlias("chassisNumber")
    private String ChassisNumber;

    @JsonProperty("НомерДвигателя")
    @JsonAlias("engineNumber")
    private String EngineNumber;

    @JsonProperty("МодельДвигателя")
    @JsonAlias("engineModel")
    private String EngineModel;

    @JsonProperty("МощностьДвигателяЛС")
    @JsonAlias("powerEngineInLs")
    private int PowerEngineInLs;

    @JsonProperty("МощностьДвигателяКвт")
    @JsonAlias("powerEngineInKvt")
    private int PowerEngineInKvt;
}