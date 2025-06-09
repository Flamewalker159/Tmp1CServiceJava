package com.tmp.tmp1cservice.dtos.TelematicsDTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GpsDto {
    @JsonAlias("Longitude")
    @JsonProperty("Долгота")
    private long Longitude;

    @JsonAlias("Latitude")
    @JsonProperty("Широта")
    private long Latitude;

    @JsonAlias("Altitude")
    @JsonProperty("Высота")
    private int Altitude;

    @JsonAlias("Course")
    @JsonProperty("Курс")
    private int Course;

    @JsonAlias("SatellitesAmount")
    @JsonProperty("ЧислоСпутников")
    private int SatellitesAmount;

    @JsonAlias("Validity")
    @JsonProperty("Валидность")
    private int Validity;

    @JsonAlias("GnssTimestamp")
    @JsonProperty("ВремяПоСпутнику")
    private long GnssTimestamp;

    @JsonAlias("Timestamp")
    @JsonProperty("ВремяСообщения")
    private long Timestamp;

    @JsonAlias("GsmCellMonitor")
    @JsonProperty("УровеньСигналаGSM")
    private int GsmCellMonitor;

    @JsonAlias("Speed")
    @JsonProperty("СкоростьGPS")
    private int Speed;

    @JsonAlias("AccVoltage")
    @JsonProperty("НапряжениеБатареи")
    private int AccVoltage;

    @JsonAlias("AirbagFired")
    @JsonProperty("СработалAirbag")
    private boolean AirbagFired;

    @JsonAlias("Odometer")
    @JsonProperty("Одометр")
    private int Odometer;

    @JsonAlias("IgnitionStatus")
    @JsonProperty("ЗажиганиеВключено")
    private boolean IgnitionStatus;

    @JsonAlias("PowerStatus")
    @JsonProperty("СостояниеБатареи")
    private boolean PowerStatus;
}