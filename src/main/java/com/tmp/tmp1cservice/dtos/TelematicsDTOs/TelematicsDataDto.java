package com.tmp.tmp1cservice.dtos.TelematicsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TelematicsDataDto {
    @JsonProperty("gps")
    private GpsDto gps;

    @JsonProperty("can")
    private CanDto can;
}