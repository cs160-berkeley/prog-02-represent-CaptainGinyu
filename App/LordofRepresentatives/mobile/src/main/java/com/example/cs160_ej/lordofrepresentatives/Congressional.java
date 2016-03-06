package com.example.cs160_ej.lordofrepresentatives;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Congressional extends AppCompatActivity
{
    protected String toAppend;
    protected int receivedZipCode;
    protected TextView congressionalHeader;

    protected Button nextButton;
    protected Button prevButton;
    protected int nextButtonColor;
    protected int prevButtonColor;

    protected int currRepIndex;

    public static ArrayList<RepresentativeInfo> dummyRepInfo;
    static
    {
        setUpDummyRepInfo();
    }

    public static void setUpDummyRepInfo()
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
        setContentView(R.layout.activity_congressional);
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        nextButton = (Button) findViewById(R.id.next);
        prevButton = (Button) findViewById(R.id.previous);
        nextButtonColor = ((ColorDrawable) nextButton.getBackground()).getColor();
        prevButtonColor = ((ColorDrawable) prevButton.getBackground()).getColor();

        receivedZipCode = -1;

        setUpDummyRepInfo();
        currRepIndex = 0;

        Intent receivedIntent = getIntent();
        if (receivedIntent != null)
        {
            Bundle extras = receivedIntent.getExtras();
            toAppend = "\n" + extras.getString("to append", "Unknown Location");
            if (toAppend.equals("\nZIP code"))
            {
                receivedZipCode = extras.getInt("zip");
                toAppend += " " + Integer.toString(receivedZipCode);
            }
            toAppend += ":";
        }

        updateRepInfo();

        congressionalHeader = (TextView) findViewById(R.id.congressionalHeader);
        congressionalHeader.setText(congressionalHeader.getText() + toAppend);

        nextButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, nextButtonColor);

                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    currRepIndex = (currRepIndex + 1) % dummyRepInfo.size();
                    updateRepInfo();
                }

                return true;
            }
        });
        prevButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                changeButtonColor(v, event, prevButtonColor);

                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    currRepIndex--;
                    if (currRepIndex < 0)
                    {
                        currRepIndex = dummyRepInfo.size() - 1;
                    }
                    updateRepInfo();
                }

                return true;
            }
        });
    }

    protected void updateRepInfo()
    {
        RepFragment currRepFragment = new RepFragment();
        Bundle argsForFragment = new Bundle();
        RepresentativeInfo representativeInfo = dummyRepInfo.get(currRepIndex);

        argsForFragment.putString("name", representativeInfo.name);
        argsForFragment.putInt("repImageReference", representativeInfo.repImageReference);
        argsForFragment.putString("party", representativeInfo.party);
        argsForFragment.putString("email", representativeInfo.email);
        argsForFragment.putString("website", representativeInfo.website);
        argsForFragment.putString("lastTweet", representativeInfo.lastTweet);
        argsForFragment.putString("index", Integer.toString(currRepIndex));

        currRepFragment.setArguments(argsForFragment);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.repInfoDisplay, currRepFragment);

        transaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        Intent toMain = new Intent(this, MainActivity.class);
        if (receivedZipCode != -1)
        {
            toMain.putExtra("received zip", (CharSequence) Integer.toString(receivedZipCode));
        }
        startActivity(toMain);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean changeButtonColor(View view, MotionEvent motionEvent, int origColor)
    {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            ((Button) view).setBackgroundColor(Color.GREEN);
        }
        else
        {
            ((Button) view).setBackgroundColor(origColor);
        }

        return true;
    }
}
