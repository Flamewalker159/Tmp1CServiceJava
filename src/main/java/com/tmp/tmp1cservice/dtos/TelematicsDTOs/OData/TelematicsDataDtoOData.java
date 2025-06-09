package com.tmp.tmp1cservice.dtos.TelematicsDTOs.OData;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TelematicsDataDtoOData {
    @JsonProperty("Period")
    private OffsetDateTime Period;

    @JsonProperty("ТС_Key")
    private UUID TcKey;

    @JsonProperty("Долгота")
    public long Longitude;

    @JsonProperty("Широта")
    public long Latitude;

    @JsonProperty("Высота")
    public int Altitude;

    @JsonProperty("Курс")
    public int Course;

    @JsonProperty("ЧислоСпутников")
    public int SatellitesAmount;

    @JsonProperty("Валидность")
    public boolean Validity;

    @JsonProperty("ВремяПоСпутнику")
    public long GnssTimestamp;

    @JsonProperty("ВремяСообщения")
    public long Timestamp;

    @JsonProperty("УровеньСигналаGSM")
    public int GsmCellMonitor;

    @JsonProperty("СкоростьGPS")
    public int SpeedGps;

    @JsonProperty("НапряжениеБатареи")
    public int AccVoltage;

    @JsonProperty("СработалAirbag")
    public boolean AirbagFired;

    @JsonProperty("Одометр")
    public int Odometer;

    @JsonProperty("СкоростьCAN")
    public int SpeedCan;

    @JsonProperty("ПробегДоСервиса")
    public int RemainingMileage;

    @JsonProperty("УровеньТоплива")
    public int FuelLevel;

    @JsonProperty("РасходТоплива")
    public int FuelConsumption;

    @JsonProperty("ТемператураОЖ")
    public int CoolantTemp;

    @JsonProperty("ДавлениеМасла")
    public int EngineOilPressure;

    @JsonProperty("НапряжениеБортовойСети")
    public int OnboardPowerVoltage;

    @JsonProperty("МаксОборотыДвигателя")
    public int MaxRpm;

    @JsonProperty("ЗажиганиеВключено")
    public boolean IgnitionStatus;

    @JsonProperty("СостояниеБатареи")
    public boolean PowerStatus;

    @JsonProperty("НизкийУровеньОЖ")
    public boolean CoolantLevelLow;

    @JsonProperty("НеисправностьГенератора")
    public boolean GeneratorMalfunction;

    @JsonProperty("НизкийУровеньТормознойЖидкости")
    public boolean BreakFluidLowLevel;

    @JsonProperty("УровеньГаза")
    public int GazLevel;

    @JsonProperty("ТипТоплива")
    public int FuelType;
}