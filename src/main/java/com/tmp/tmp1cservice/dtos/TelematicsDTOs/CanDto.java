package com.tmp.tmp1cservice.dtos.TelematicsDTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CanDto {

    @JsonAlias("Speed")
    @JsonProperty("СкоростьCAN")
    private int Speed;

    @JsonAlias("RemainingMileage")
    @JsonProperty("ПробегДоСервиса")
    private int RemainingMileage;

    @JsonAlias("FuelLevel")
    @JsonProperty("УровеньТоплива")
    private int FuelLevel;

    @JsonAlias("FuelConsumption")
    @JsonProperty("РасходТоплива")
    private int FuelConsumption;

    @JsonAlias("CoolantTemp")
    @JsonProperty("ТемператураОЖ")
    private int CoolantTemp;

    @JsonAlias("EngineOilPressure")
    @JsonProperty("ДавлениеМасла")
    private int EngineOilPressure;

    @JsonAlias("OnboardPowerVoltage")
    @JsonProperty("НапряжениеБортовойСети")
    private int OnboardPowerVoltage;

    @JsonAlias("MaxRpm")
    @JsonProperty("МаксОборотыДвигателя")
    private int MaxRpm;

    @JsonAlias("CoolantLevelLow")
    @JsonProperty("НизкийУровеньОЖ")
    private boolean CoolantLevelLow;

    @JsonAlias("GeneratorMalfunction")
    @JsonProperty("НеисправностьГенератора")
    private boolean GeneratorMalfunction;

    @JsonAlias("BreakFluidLowLevel")
    @JsonProperty("НизкийУровеньТормознойЖидкости")
    private boolean BreakFluidLowLevel;

    @JsonAlias("GazLevel")
    @JsonProperty("УровеньГаза")
    private int GazLevel;

    @JsonAlias("FuelType")
    @JsonProperty("ТипТоплива")
    public int FuelType;
}