package com.gui.mdt.thongsieknavclient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yeqim_000 on 16/08/16.
 */
public class DataConverter {
    public static Long currTimeStamp = System.currentTimeMillis();
    public static int timeConversion = 86400000;    //24 * 60 * 60 * 1000

    public static String ConvertDateToYearMonthDay(String dateString)
    {
        if(dateString !=  null && dateString.length() > 0)
        {
            String[] splitString = dateString.toString().split("/");
            dateString = splitString[2] + "/" + splitString[1] + "/" + splitString[0];
            return dateString;
        }
        else
        {
            return "";
        }
    }

    public static String ConvertCalendarToDayMonthYear(Date date)
    {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        return simpleDateFormat.format(date);
    }

    public static String ConvertJsonDateToDayMonthYear(String date)
    {
        if(date.contains("0001-"))
        {
            return "";
        }
        else
        {
            String[] splitString = date.split("T")[0].split("-");
            return splitString[2] + "/" + splitString[1] + "/" + splitString[0];
        }
    }


    public static String ConvertJsonDateToYearMonthDay(String date)
    {
        if(date.contains("0001-"))
        {
            return "";
        }
        else
        {
            String[] splitString = date.split("T")[0].split("-");
            return splitString[0] + "-" + splitString[1] + "-" + splitString[2];
        }
    }

    public static String CheckNullString(String s, String prefix)
    {
        if(s == null)
        {
            return "";
        }
        else
        {
            return prefix + s;
        }
    }

    public static String GetJsonTime(String date)
    {
        if(date.contains("0001-"))
        {
            return "";
        }
        else
        {
            String splitString = date.split("T")[1];
            return splitString.substring(0, 5);
        }
    }

    public static String GetNormalTimePortion(String date) //FORMAT: 10/06/16 01:10 PM
    {
        return date.substring(10, date.length());
    }

    public static String GetNormalDatePortion(String date) //FORMAT: 10/06/16 01:10 PM
    {
        String splitString = date.split(" ")[0];
        return splitString;
    }

    public static String ConvertDateToDayMonthYear(String date)     // FORMAT: 10/30/16 -> 30/10/2016
    {
        String[] splitString = date.split(" ")[0].split("/");
        String dateString = splitString[1] + "/" + splitString[0] + "/" + splitString[2];
        return dateString;
    }

    public static int getDaysBetweenTimeRange(Long prevTimeStamp)
    {
        // Calculate difference in milliseconds
        long diff = Math.abs(currTimeStamp - prevTimeStamp);

        return (int)(diff / timeConversion);
    }

}
