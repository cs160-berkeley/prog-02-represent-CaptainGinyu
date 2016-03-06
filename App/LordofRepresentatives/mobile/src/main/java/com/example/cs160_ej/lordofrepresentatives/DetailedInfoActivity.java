package com.example.cs160_ej.lordofrepresentatives;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class DetailedInfoActivity extends AppCompatActivity
{
    protected TextView nameText;
    protected TextView partyText;
    protected TextView emailText;
    protected TextView committeeText;
    protected TextView billsText;
    protected TextView endOfTermText;

    protected ImageView repImage;
    protected ImageView partyImage;

    protected HashMap<String, Drawable> partiesToLogos;
    protected HashMap<String, Integer> partiesToColors;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_info);

        Context context = getBaseContext();
        partiesToLogos = new HashMap<String, Drawable>();
        partiesToLogos.put("Republican", ContextCompat.getDrawable(context, R.drawable.republican));
        partiesToLogos.put("Democrat", ContextCompat.getDrawable(context, R.drawable.democrat));
        partiesToLogos.put("Unknown Party", ContextCompat.getDrawable(context, R.drawable.unknown));
        partiesToLogos.put("Independent", ContextCompat.getDrawable(context, R.drawable.unknown));

        partiesToColors = new HashMap<String, Integer>();
        partiesToColors.put("Republican", Color.rgb(255, 125, 125));
        partiesToColors.put("Democrat", Color.rgb(125, 150, 255));
        partiesToColors.put("Unknown Party", Color.rgb(255, 245, 118));
        partiesToColors.put("Independent", Color.rgb(255, 245, 118));

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        nameText = (TextView) findViewById(R.id.repName);
        partyText = (TextView) findViewById(R.id.partyName);
        repImage = (ImageView) findViewById(R.id.repImage);
        partyImage = (ImageView) findViewById(R.id.partyImage);
        endOfTermText = (TextView) findViewById(R.id.endOfTerm);
        emailText = (TextView) findViewById(R.id.repEmail);
        committeeText = (TextView) findViewById(R.id.committee);
        billsText = (TextView) findViewById(R.id.billsList);

        Intent receivedIntent = getIntent();

        if (receivedIntent != null)
        {
            Bundle extras = receivedIntent.getExtras();

            if (extras != null)
            {
                int index = Integer.parseInt(extras.getString("index"));

                if (index > -1)
                {
                    RepresentativeInfo repInfo = Congressional.dummyRepInfo.get(index);
                    DetailedInfo repDetailedInfo = repInfo.detailedInfo;
                    nameText.setText(repInfo.name);
                    partyText.setText(repInfo.party);
                    repImage.setImageResource(repInfo.repImageReference);
                    partyImage.setImageDrawable(partiesToLogos.get(repInfo.party));
                    endOfTermText.setText(repDetailedInfo.endOfTermDate);
                    emailText.setText(repInfo.email);
                    committeeText.setText("Committee: " + repDetailedInfo.committeeName);
                    String billsTextContent = "";

                    for (String key : repDetailedInfo.billsAndDates.keySet())
                    {
                        billsTextContent += key + ": " + repDetailedInfo.billsAndDates.get(key) + "\n";
                    }
                    billsText.setText(billsTextContent);
                }
            }
        }
    }
}
