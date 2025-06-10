package com.tmp.tmp1cservice.dtos.VehicleDTOs.OData;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tmp.tmp1cservice.utils.LocalDateTimeTo1CSerializerOData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleCreateDtoOData {
    @JsonProperty("Ref_Key")
    @JsonAlias("refKey")
    private UUID RefKey;

    @JsonProperty("Code")
    @JsonAlias("code1C")
    private String Code;

    @JsonProperty("Description")
    @JsonAlias("name")
    private String Description;

    @JsonProperty("ГосНомер")
    @JsonAlias("licensePlate")
    private String LicensePlate;

    @JsonProperty("VIN")
    @JsonAlias("vin")
    private String Vin;

    @JsonProperty("Марка")
    @JsonAlias("brand")
    private String Brand;

    @JsonProperty("Модель")
    @JsonAlias("model")
    private String Model;

    @JsonProperty("ГруппаТС")
    @JsonAlias("groupId")
    private String Group;

    @JsonProperty("ГодВыпуска")
    @JsonAlias("yearOfIssue")
    @JsonSerialize(using = LocalDateTimeTo1CSerializerOData.class)
    private LocalDateTime Year;

    @JsonProperty("Масса")
    @JsonAlias("mass")
    private String Mass;

    @JsonProperty("Габариты")
    @JsonAlias("dimensions")
    private String Dimensions;

    @JsonProperty("ТипВладенияТС")
    @JsonAlias("ownershipType")
    private String OwnershipType;

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
    private String PowerEngineInLs;

    @JsonProperty("МощностьДвигателяКвт")
    @JsonAlias("powerEngineInKvt")
    private String PowerEngineInKvt;
}
