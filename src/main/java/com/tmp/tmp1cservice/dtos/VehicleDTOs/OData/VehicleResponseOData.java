package com.tmp.tmp1cservice.dtos.VehicleDTOs.OData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmp.tmp1cservice.dtos.VehicleDTOs.OData.VehicleDtoOData;
import lombok.Data;

import java.util.List;

@Data
public class VehicleResponseOData
{
    @JsonProperty("value")
    private List<VehicleDtoOData> Vehicles;
}