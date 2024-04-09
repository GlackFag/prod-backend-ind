package com.glackfag.travelgentle.util;


import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public abstract class TimeUtils {
    public static Date dateFromLocalDate(LocalDate localDate, ZoneId zoneId){
        ZonedDateTime localDateTime = ZonedDateTime.of(localDate, LocalTime.MIN, zoneId);

        return new Date(localDateTime.toInstant().toEpochMilli());
    }
}
