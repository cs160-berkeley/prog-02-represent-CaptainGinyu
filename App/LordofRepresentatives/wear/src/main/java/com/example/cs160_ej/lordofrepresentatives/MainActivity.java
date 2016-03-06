package com.example.cs160_ej.lordofrepresentatives;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;

import java.util.ArrayList;

public class MainActivity extends Activity
{
    public static ArrayList<RepresentativeInfo> dummyRepInfo;
    static
    {
        dummyRepInfo = new ArrayList<RepresentativeInfo>();
        RepresentativeInfo rep1 = new RepresentativeInfo(
                "Ian McDiarmid",
                R.drawable.palpatine,
                "Republican",
                "willBeEmperor@hotmail.com",
                "emperorpalpatine.com",
                "Power!!! Unlimited... POWER!!!",
                new DetailedInfo("May 4, 2066", "The Empire"));
        rep1.detailedInfo.billsAndDates.put("2005", "Order 66");
        rep1.detailedInfo.billsAndDates.put("2006", "Death Star Law");
        rep1.detailedInfo.billsAndDates.put("2010", "Anti-Jedi Law");
        RepresentativeInfo rep2 = new RepresentativeInfo(
                "Eric Paulos",
                R.drawable.paulos,
                "Democrat",
                "paulos@paulos.gov",
                "yourdesignisbad.com",
                "I bet that your app is full of bad design.",
                new DetailedInfo("December 31, 2345", "HCI Committee"));
        rep2.detailedInfo.billsAndDates.put("2004", "Anti-Bad-Design Bill");
        rep2.detailedInfo.billsAndDates.put("2005", "Anti-Bad-Design Bill Beta");
        rep2.detailedInfo.billsAndDates.put("2017", "The Good Design Initiative");
        RepresentativeInfo rep3 = new RepresentativeInfo(
                "Donald Duck",
                R.drawable.donald_duck,
                "Independent",
                "makedisneygreatagain@makedisneygreatagain.com",
                "makedisneygreatagain.com",
                "Quack",
                new DetailedInfo("January 22, 3000", "The Grand Duck Legion"));
        rep3.detailedInfo.billsAndDates.put("2052", "Anti-Daffy-Duck Bill");
        rep3.detailedInfo.billsAndDates.put("2055", "Ultimate Quack Bill");
        dummyRepInfo.add(rep1);
        dummyRepInfo.add(rep2);
        dummyRepInfo.add(rep3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener()
        {
            @Override
            public void onLayoutInflated(WatchViewStub stub)
            {

            }
        });
    }
}
