package com.tmp.tmp1cservice.dtos.VehicleDTOs.OData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleResponseOData
{
    @JsonProperty("value")
    private List<VehicleDtoOData> Vehicles;
}