package com.example.cs160_ej.lordofrepresentatives;

import java.util.HashMap;

public class DetailedInfo
{
    public String endOfTermDate;
    public String committeeName;
    public HashMap<String, String> billsAndDates;

    public DetailedInfo(String endOfTermDate, String committeeName)
    {
        this.endOfTermDate = endOfTermDate;
        this.committeeName = committeeName;
        this.billsAndDates = new HashMap<String, String>();
    }
}
