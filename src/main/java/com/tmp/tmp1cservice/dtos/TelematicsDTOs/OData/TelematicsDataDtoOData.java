package com.tmp.tmp1cservice.dtos.TelematicsDTOs.OData;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tmp.tmp1cservice.utils.LocalDateTimeTo1CSerializerOData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TelematicsDataDtoOData {
    @JsonProperty("Period")
    @JsonAlias("period")
    @JsonSerialize(using = LocalDateTimeTo1CSerializerOData.class)
    private LocalDateTime Period;

    @JsonProperty("ТС_Key")
    @JsonAlias("tcKey")
    private UUID TcKey;

    @JsonProperty("Долгота")
    @JsonAlias("longitude")
    private long Longitude;

    @JsonProperty("Широта")
    @JsonAlias("latitude")
    private long Latitude;

    @JsonProperty("Высота")
    @JsonAlias("altitude")
    private int Altitude;

    @JsonProperty("Курс")
    @JsonAlias("course")
    private int Course;

    @JsonProperty("ЧислоСпутников")
    @JsonAlias("satellitesAmount")
    private int SatellitesAmount;

    @JsonProperty("Валидность")
    @JsonAlias("validity")
    private boolean Validity;

    @JsonProperty("ВремяПоСпутнику")
    @JsonAlias("gnssTimestamp")
    private long GnssTimestamp;

    @JsonProperty("ВремяСообщения")
    @JsonAlias("timestamp")
    private long Timestamp;

    @JsonProperty("УровеньСигналаGSM")
    @JsonAlias("gsmCellMonitor")
    private int GsmCellMonitor;

    @JsonProperty("СкоростьGPS")
    @JsonAlias("speedGps")
    private int SpeedGps;

    @JsonProperty("НапряжениеБатареи")
    @JsonAlias("accVoltage")
    private int AccVoltage;

    @JsonProperty("СработалAirbag")
    @JsonAlias("airbagFired")
    private boolean AirbagFired;

    @JsonProperty("Одометр")
    @JsonAlias("odometer")
    private int Odometer;

    @JsonProperty("СкоростьCAN")
    @JsonAlias("speedCan")
    private int SpeedCan;

    @JsonProperty("ПробегДоСервиса")
    @JsonAlias("remainingMileage")
    private int RemainingMileage;

    @JsonProperty("УровеньТоплива")
    @JsonAlias("fuelLevel")
    private int FuelLevel;

    @JsonProperty("РасходТоплива")
    @JsonAlias("fuelConsumption")
    private int FuelConsumption;

    @JsonProperty("ТемператураОЖ")
    @JsonAlias("coolantTemp")
    private int CoolantTemp;

    @JsonProperty("ДавлениеМасла")
    @JsonAlias("engineOilPressure")
    private int EngineOilPressure;

    @JsonProperty("НапряжениеБортовойСети")
    @JsonAlias("onboardPowerVoltage")
    private int OnboardPowerVoltage;

    @JsonProperty("МаксОборотыДвигателя")
    @JsonAlias("maxRpm")
    private int MaxRpm;

    @JsonProperty("ЗажиганиеВключено")
    @JsonAlias("ignitionStatus")
    private boolean IgnitionStatus;

    @JsonProperty("СостояниеБатареи")
    @JsonAlias("powerStatus")
    private boolean PowerStatus;

    @JsonProperty("НизкийУровеньОЖ")
    @JsonAlias("coolantLevelLow")
    private boolean CoolantLevelLow;

    @JsonProperty("НеисправностьГенератора")
    @JsonAlias("generatorMalfunction")
    private boolean GeneratorMalfunction;

    @JsonProperty("НизкийУровеньТормознойЖидкости")
    @JsonAlias("breakFluidLowLevel")
    private boolean BreakFluidLowLevel;

    @JsonProperty("УровеньГаза")
    @JsonAlias("gazLevel")
    private int GazLevel;

    @JsonProperty("ТипТоплива")
    @JsonAlias("fuelType")
    private int FuelType;
}