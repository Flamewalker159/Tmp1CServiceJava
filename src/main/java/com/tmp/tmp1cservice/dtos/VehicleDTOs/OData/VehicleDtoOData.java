package com.tmp.tmp1cservice.dtos.VehicleDTOs.OData;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.UUID;

public class VehicleDtoOData {
    @JsonProperty("Ref_Key")
    public UUID RefKey;

    @JsonProperty("Code")
    public String Code;

    @JsonProperty("Description")
    public String Description;

    @JsonProperty("ГосНомер")
    public String LicensePlate;

    @JsonProperty("VIN")
    public String Vin;

    @JsonProperty("Марка")
    public String Brand;

    @JsonProperty("Модель")
    public String Model;

    @JsonProperty("ГруппаТС")
    public String Group;

    @JsonProperty("ГодВыпуска")
    public Date Year;

    @JsonProperty("Масса")
    public String Mass;

    @JsonProperty("Габариты")
    public String Dimensions;

    @JsonProperty("ТипВладенияТС")
    public String OwnershipType;

    @JsonProperty("НомерШасси")
    public String ChassisNumber;

    @JsonProperty("НомерДвигателя")
    public String EngineNumber;

    @JsonProperty("МодельДвигателя")
    public String EngineModel;

    @JsonProperty("МощностьДвигателяЛС")
    public String PowerEngineInLs;

    @JsonProperty("МощностьДвигателяКвт")
    public String PowerEngineInKvt;
}