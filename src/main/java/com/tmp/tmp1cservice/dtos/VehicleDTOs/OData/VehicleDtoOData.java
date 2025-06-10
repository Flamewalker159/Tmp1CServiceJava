package com.tmp.tmp1cservice.dtos.VehicleDTOs.OData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDtoOData {
    @JsonProperty("Ref_Key")
    private UUID RefKey;

    @JsonProperty("Code")
    private String Code;

    @JsonProperty("Description")
    private String Description;

    @JsonProperty("ГосНомер")
    private String LicensePlate;

    @JsonProperty("VIN")
    private String Vin;

    @JsonProperty("Марка")
    private String Brand;

    @JsonProperty("Модель")
    private String Model;

    @JsonProperty("ГруппаТС")
    private String Group;

    @JsonProperty("ГодВыпуска")
    private Date Year;

    @JsonProperty("Масса")
    private String Mass;

    @JsonProperty("Габариты")
    private String Dimensions;

    @JsonProperty("ТипВладенияТС")
    private String OwnershipType;

    @JsonProperty("НомерШасси")
    private String ChassisNumber;

    @JsonProperty("НомерДвигателя")
    private String EngineNumber;

    @JsonProperty("МодельДвигателя")
    private String EngineModel;

    @JsonProperty("МощностьДвигателяЛС")
    private String PowerEngineInLs;

    @JsonProperty("МощностьДвигателяКвт")
    private String PowerEngineInKvt;
}